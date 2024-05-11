ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "ScalaBaz",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.2.10",
      "com.typesafe.akka" %% "akka-stream" % "2.6.16",
      "org.mongodb.scala" %% "mongo-scala-driver" % "4.4.0",
      "de.heikoseeberger" %% "akka-http-json4s" % "1.37.0",
      "org.json4s" %% "json4s-native" % "4.0.3",
      "org.json4s" %% "json4s-jackson" % "4.0.3",
      "com.typesafe.akka" %% "akka-actor" % "2.6.16",
      "com.rabbitmq" % "amqp-client" % "5.16.0",
      "io.circe" %% "circe-core" % "0.14.1",
      "io.circe" %% "circe-generic" % "0.14.1",
      "io.circe" %% "circe-parser" % "0.14.1"


    )
  )
ThisBuild / assemblyMergeStrategy in assembly := {
  case PathList("google", "protobuf", "any.proto") => MergeStrategy.first
  case PathList("google", "protobuf", "descriptor.proto") => MergeStrategy.first
  case PathList("google", "protobuf", "empty.proto") => MergeStrategy.first
  case PathList("google", "protobuf", "struct.proto") => MergeStrategy.first
  case PathList("module-info.class") => MergeStrategy.first // Здесь происходит изменение
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
