package io.scalac.lab.api.tapir

import io.circe.generic.auto._
import io.scalac.lab.api.model.{Address, Apartment, ApiError, Paging}
import io.scalac.lab.api.security.Security.ApiKey
import io.scalac.lab.api.tapir.QueryStringParams._
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.PartialServerEndpoint

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
trait ApartmentsEndpointsDefinition[F[_]] extends SecuritySupport[F] with CustomStatusMappings {

  val listApartments: PartialServerEndpoint[ApiKey, Paging, ApiError, List[Apartment], Nothing, F] =
    securedWithStatuses(BadRequest).get
      .in("v1" / "data" / "apartments")
      .in(pagingIn)
      .out(
        jsonBody[List[Apartment]]
          .description("A list of apartments")
          .example(
            List(
              Apartment(Some(100), Address("Poznan", "Gorna Wilda", "3"), "City of Poznan", 250000),
              Apartment(Some(101), Address("Warsaw", "Marszalkowska", "1"), "City of Warsaw", 500000)
            )
          )
      )
      .description("An endpoint responsible for listing all available apartments")

  val getApartment: PartialServerEndpoint[ApiKey, Int, ApiError, Apartment, Nothing, F] =
    securedWithStatuses(NotFound).get
      .in("v1" / "data" / "apartments")
      .in(
        path[Int]("id")
          .description("The identifier of the apartment to be found")
          .example(101))
      .out(
        jsonBody[Apartment]
          .description("An apartment found for given id")
          .example(Apartment(Some(101), Address("Warsaw", "Marszalkowska", "1"), "City of Warsaw", 500000))
      )
      .description("An endpoint responsible for getting specific apartment by id")

  val findApartment: PartialServerEndpoint[ApiKey, Address, ApiError, Apartment, Nothing, F] =
    securedWithStatuses(NotFound, BadRequest).get
      .in("v1" / "data" / "apartments" / "search")
      .in(addressIn)
      .out(
        jsonBody[Apartment]
          .description("An apartment found for query string parameters")
          .example(Apartment(Some(1), Address("Warsaw", "Pulawska", "10A"), "City of Warsaw", 100000))
      )
      .description("An endpoint responsible for finding specific apartment by search predicates")

  val addApartment: PartialServerEndpoint[ApiKey, Apartment, ApiError, Apartment, Nothing, F] =
    securedWithStatuses(BadRequest).post
      .in("v1" / "data" / "apartments")
      .in(
        jsonBody[Apartment]
          .description("An apartment to be added in the storage")
          .example(Apartment(None, Address("Wroclaw", "Kutrzeby", "11"), "Jan Nowak", 100000))
      )
      .out(
        jsonBody[Apartment]
          .description("An apartment saved in the storage")
          .example(Apartment(Some(102), Address("Wroclaw", "Kutrzeby", "11"), "Jan Nowak", 100000))
      )
      .description("An endpoint responsible for adding new apartment")

  val deleteApartment: PartialServerEndpoint[ApiKey, Int, ApiError, Apartment, Nothing, F] =
    securedWithStatuses(NotFound).delete
      .in("v1" / "data" / "apartments")
      .in(
        path[Int]("id")
          .description("The identifier of the apartment to be deleted")
          .example(100))
      .out(
        jsonBody[Apartment]
          .description("An apartment deleted")
          .example(Apartment(Some(100), Address("Poznan", "Gorna Wilda", "3"), "City of Poznan", 250000))
      )
      .description("An endpoint responsible for deleting apartment by id")

}
