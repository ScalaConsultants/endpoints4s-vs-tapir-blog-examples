import sbt._

object Dependencies {

  object Versions {
    val endpoints = "1.1.0"
    val tapir = "0.16.14"
    val circe = "0.13.0"
    val scalaTest = "3.2.0"
    val akka = "2.6.8"
    val akkaHttp = "10.2.0"
    val akkaCirce = "1.31.0"
  }

  val endpoints = Seq(
    "org.endpoints4s" %% "algebra" % Versions.endpoints,
    "org.endpoints4s" %% "json-schema-generic" % Versions.endpoints,
    "org.endpoints4s" %% "akka-http-server" % "2.0.0"
  )

  val tapir = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % Versions.tapir excludeAll ExclusionRule("com.typesafe.akka"),
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http" % Versions.tapir excludeAll ExclusionRule("com.typesafe.akka")
  )

  val akka = Seq(
    "com.typesafe.akka" %% "akka-actor" % Versions.akka,
    "com.typesafe.akka" %% "akka-stream" % Versions.akka,
    "com.typesafe.akka" %% "akka-http" % Versions.akkaHttp,
    "com.typesafe.akka" %% "akka-testkit" % Versions.akka % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp % Test
  )

  val akkaCirce = Seq(
    "de.heikoseeberger" %% "akka-http-circe" % Versions.akkaCirce
  )

  val circe = Seq(
    "io.circe" %% "circe-generic" % Versions.circe
  )

  val scalaTest = Seq(
    "org.scalatest" %% "scalatest" % Versions.scalaTest % Test
  )
}
