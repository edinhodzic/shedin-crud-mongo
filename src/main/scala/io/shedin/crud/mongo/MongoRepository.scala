package io.shedin.crud.mongo

import io.shedin.crud.lib.CrudRepository
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, document}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// TODO derive collection name from T
// TODO maybe should just mix in AsyncCrudOperations[T] instead of CrudRepository[T]
abstract class MongoRepository[T]
(databaseName: String)
(implicit writer: BSONDocumentWriter[T], reader: BSONDocumentReader[T], manifest: Manifest[T])
  extends CrudRepository[T] {

  private lazy val mongoUri = s"mongodb://localhost:27017/$databaseName?authMode=scram-sha1"
  private lazy val eventualMongoConnection = Future.fromTry(MongoConnection.parseURI(mongoUri).map(MongoDriver().connection(_)))

  private def mongoDatabase: Future[DefaultDB] = eventualMongoConnection.flatMap(_.database(databaseName))

  private def mongoCollection = mongoDatabase.map(_.collection[BSONCollection](simpleName(manifest)))

  override def create(resource: T): Future[T] =
    mongoCollection.flatMap { bsonCollection =>
      bsonCollection.insert(resource).map { writeResult =>
        if (writeResult.n == 1) resource
        else throw new RuntimeException(s"failed to persist resource [$resource]")
      }
    }

  override def read(resourceId: String): Future[Option[T]] = {
    mongoCollection.flatMap(_.find(document("id" -> resourceId)).cursor[T]().collect[List]()) map (_.headOption)
  }

  override def update(resourceId: String, resource: T): Future[Option[T]] = {
    mongoCollection.flatMap(_.update(document("id" -> resourceId), resource)) map (_.n == 1)
    mongoCollection.flatMap(_.update(document("id" -> resourceId), resource)) map { updateWriteResult =>
      if (updateWriteResult.n == 0) None
      else if (updateWriteResult.n == 1) Some(resource)
      else throw new RuntimeException("more than one doc update") // TODO revisit this

    }
  }

  override def update(resourceId: String, updatePayload: String): Future[Option[AnyRef]] = throw new UnsupportedOperationException

  override def delete(resourceId: String): Future[Boolean] =
    mongoCollection.flatMap(_.remove(document("id" -> resourceId))) map (_.n > 0)

  private def simpleName[T](manifest: Manifest[T]): String =
    manifest.runtimeClass.getSimpleName.toLowerCase

}
