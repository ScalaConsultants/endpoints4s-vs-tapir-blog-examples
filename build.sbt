name := "lib-comparison"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies :=
  Dependencies.endpoints ++
    Dependencies.tapir ++
    Dependencies.akka ++
    Dependencies.circe ++
    Dependencies.scalaTest

addCommandAlias("runTapirApi", "runMain io.scalac.lab.api.tapir.ApartmentsApi")
addCommandAlias("runEndpoints4sApi", "runMain io.scalac.lab.api.endpoints4s.ApartmentsApi")
