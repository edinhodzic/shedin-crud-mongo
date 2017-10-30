import bintray.Keys._
import sbt.Keys.resolvers
import sbt._

organization := "io.shedin.library"

name := "shedin-crud-mongo"

version := "0.2.1"

lazy val shedinCrudMongo = project.in(file("."))
  .settings(bintrayPublishSettings: _*)
  .settings(
    sbtPlugin := false,
    licenses += ("Apache-2.0", url("https://opensource.org/licenses/Apache-2.0")),
    publishMavenStyle := false,
    repository in bintray := "shedin",
    bintrayOrganization in bintray := None,
    resolvers += Resolver.url("edinhodzic", url("http://dl.bintray.com/edinhodzic/shedin"))(Resolver.ivyStylePatterns)
  )
  .settings(name := "shedin-crud-mongo")
  .settings(scalaVersion := "2.12.2")
  .settings(libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-library" % "2.12.2",
    "io.shedin.library" % "shedin-crud-lib_2.12" % "0.2.1",
    "org.reactivemongo" % "reactivemongo_2.12" % "0.12.2"))
  .settings(scalacOptions ++= Seq("-deprecation", "-feature"))
  .settings(ivyScala := ivyScala.value map {
    _.copy(overrideScalaVersion = true)
  })
