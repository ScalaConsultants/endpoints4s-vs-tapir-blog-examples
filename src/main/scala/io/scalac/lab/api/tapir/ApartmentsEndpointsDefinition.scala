package io.scalac.lab.api.tapir

import io.circe.generic.auto._
import io.scalac.lab.api.model.Apartment
import sttp.tapir._
import sttp.tapir.json.circe._

/**
  * Defines an HTTP endpoint for managing apartments in Endpoints4s.
  *
  * This web service has 5 endpoints:
  * - first for listing apartments,
  * - second for getting apartment by id,
  * - third for finding apartment by address,
  * - fourth for adding new apartment,
  * - fifth for deleting apartment by id.
  */
trait ApartmentsEndpointsDefinition {

  val listApartments: Endpoint[Unit, String, List[Apartment], Nothing] =
    endpoint.get
      .in("v1" / "data" / "apartments")
      .errorOut(stringBody.description("An error message, when something went wrong"))
      .out(
        jsonBody[List[Apartment]]
          .description("A list of apartments")
          .example(
            List(
              Apartment(Some(100), "Poznan", "Gorna Wilda", "3", "City of Poznan", 250000),
              Apartment(Some(101), "Warsaw", "Marszalkowska", "1", "City of Warsaw", 500000)
            ))
      )
      .description("An endpoint responsible for listing all available apartments")

  val getApartment: Endpoint[Int, String, Apartment, Nothing] =
    endpoint.get
      .in("v1" / "data" / "apartments")
      .in(
        path[Int]("id")
          .description("The identifier of the apartment to be found")
          .example(101))
      .errorOut(stringBody.description("An error message, when something went wrong or apartment could not be found"))
      .out(
        jsonBody[Apartment]
          .description("An apartment found for given id")
          .example(Apartment(Some(101), "Warsaw", "Marszalkowska", "1", "City of Warsaw", 500000))
      )
      .description("An endpoint responsible for getting specific apartment by id")

  val findApartment: Endpoint[(String, String, String), String, Apartment, Nothing] =
    endpoint.get
      .in("v1" / "data" / "apartments" / "search")
      .in(query[String]("city")
        .description("A city we want to find apartment for")
        .example("Warsaw"))
      .in(query[String]("street")
        .description("A street we want to find apartment for")
        .example("Pulawska"))
      .in(query[String]("number")
        .description("A street number we want to find apartment for")
        .example("10A"))
      .errorOut(stringBody.description("An error message, when something went wrong or apartment could not be found"))
      .out(
        jsonBody[Apartment]
          .description("An apartment found for query string parameters")
          .example(Apartment(Some(1), "Warsaw", "Pulawska", "10A", "City of Warsaw", 100000))
      )
      .description("An endpoint responsible for finding specific apartment by search predicates")

  val addApartment: Endpoint[Apartment, String, Apartment, Nothing] =
    endpoint.post
      .in("v1" / "data" / "apartments")
      .in(
        jsonBody[Apartment]
          .description("An apartment to be added in the storage")
          .example(Apartment(None, "Wroclaw", "Kutrzeby", "11", "Jan Nowak", 100000))
      )
      .errorOut(stringBody.description("An error message, when something went wrong while saving apartment"))
      .out(
        jsonBody[Apartment]
          .description("An apartment saved in the storage")
          .example(Apartment(Some(102), "Wroclaw", "Kutrzeby", "11", "Jan Nowak", 100000))
      )
      .description("An endpoint responsible for adding new apartment")

  val deleteApartment: Endpoint[Int, String, Apartment, Nothing] =
    endpoint.delete
      .in("v1" / "data" / "apartments")
      .in(
        path[Int]("id")
          .description("The identifier of the apartment to be deleted")
          .example(100))
      .errorOut(stringBody.description("An error message, when something went wrong or apartment could not be found"))
      .out(
        jsonBody[Apartment]
          .description("An apartment deleted")
          .example(Apartment(Some(100), "Poznan", "Gorna Wilda", "3", "City of Poznan", 250000))
      )
      .description("An endpoint responsible for deleting apartment by id")

}
