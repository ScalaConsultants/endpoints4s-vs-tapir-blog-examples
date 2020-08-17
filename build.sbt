name := "lib-comparison"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies :=
  Dependencies.endpoints ++
    Dependencies.tapir ++
    Dependencies.akka ++
    Dependencies.circe ++
    Dependencies.scalaTest
