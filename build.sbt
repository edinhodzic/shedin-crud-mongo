import sbt._

organization := "io.shedin.library"

name := "shedin-crud-mongo"

version := "0.1.0-SNAPSHOT"

lazy val shedinCrudMongo = project.in(file("."))
  .settings(name := "shedin-crud-mongo")
  .settings(scalaVersion := "2.11.8")
  .settings(libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-library" % "2.11.8",
  "io.shedin.library" % "shedin-crud-lib_2.11" % "0.1.0-SNAPSHOT",
  "org.reactivemongo" % "reactivemongo_2.11" % "0.12.0"))
  .settings(scalacOptions ++= Seq("-deprecation", "-feature"))
  .settings(ivyScala := ivyScala.value map {
    _.copy(overrideScalaVersion = true)
  })
