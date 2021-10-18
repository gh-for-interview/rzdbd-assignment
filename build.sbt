name := "RZDBDTestAssignment"

version := "0.1"

scalaVersion := "2.12.15"

val akkaHttpVersion = "10.1.13"

libraryDependencies ++= Seq(
  "io.github.shogowada" %% "scala-json-rpc" % "0.9.3",
  "io.github.shogowada" %% "scala-json-rpc-upickle-json-serializer" % "0.9.3",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  "com.typesafe" % "config" % "1.3.3",
  "com.typesafe.akka" %% "akka-stream" % "2.5.32",
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-testkit" % "2.5.31" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "org.scalatest" %% "scalatest" % "3.1.2" % Test
)