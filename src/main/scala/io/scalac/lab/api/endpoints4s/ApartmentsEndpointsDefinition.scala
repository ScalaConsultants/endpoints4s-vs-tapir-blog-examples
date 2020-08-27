package io.scalac.lab.api.endpoints4s

import endpoints4s.algebra.{Endpoints, JsonEntitiesFromSchemas}
import endpoints4s.generic.JsonSchemas
import io.scalac.lab.api.model.{Address, Apartment, ApiError, Paging}
import io.scalac.lab.api.security.Security.ApiKey

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
trait ApartmentsEndpointsDefinition
    extends Endpoints
    with JsonEntitiesFromSchemas
    with JsonSchemas
    with SecuritySupport
    with QueryStringParams
    with CustomStatusCodes {

  private implicit val addressSchema: JsonSchema[Address] = genericJsonSchema
  private implicit val apartmentSchema: JsonSchema[Apartment] = genericJsonSchema

  val listApartments: Endpoint[(Paging, ApiKey), Either[ApiError, List[Apartment]]] =
    authenticatedEndpoint(
      request = get(path / "v1" / "data" / "apartments" /? pagingQueryString),
      response = withStatusCodes(ok(jsonResponse[List[Apartment]], Some("A list of apartments")), Unauthorized, BadRequest),
      docs = EndpointDocs().withDescription(Some("An endpoint responsible for listing all available apartments"))
    )

  val getApartment: Endpoint[(Int, ApiKey), Either[ApiError, Apartment]] =
    authenticatedEndpoint(
      request = get(path / "v1" / "data" / "apartments" / segment[Int]("id")),
      response = withStatusCodes(ok(jsonResponse[Apartment], Some("An apartment found for given id")), Unauthorized, NotFound),
      docs = EndpointDocs().withDescription(Some("An endpoint responsible for getting specific apartment by id"))
    )

  val findApartment: Endpoint[(Address, ApiKey), Either[ApiError, Apartment]] =
    authenticatedEndpoint(
      request = get(path / "v1" / "data" / "apartments" / "search" /? addressQueryString),
      response = withStatusCodes(ok(jsonResponse[Apartment], Some("An apartment found for query string parameters")),
                                 Unauthorized,
                                 BadRequest,
                                 NotFound),
      docs = EndpointDocs().withDescription(Some("An endpoint responsible for finding specific apartment by search predicates"))
    )

  val addApartment: Endpoint[(Apartment, ApiKey), Either[ApiError, Apartment]] =
    authenticatedEndpoint(
      request = post(path / "v1" / "data" / "apartments", jsonRequest[Apartment], Some("An apartment to be added in the storage")),
      response = withStatusCodes(ok(jsonResponse[Apartment], Some("An apartment saved in the storage")), Unauthorized, BadRequest),
      docs = EndpointDocs().withDescription(Some("An endpoint responsible for adding new apartment"))
    )

  val deleteApartment: Endpoint[(Int, ApiKey), Either[ApiError, Apartment]] =
    authenticatedEndpoint(
      request = delete(path / "v1" / "data" / "apartments" / segment[Int]("id", Some("The identifier of the apartment to be deleted"))),
      response = withStatusCodes(ok(jsonResponse[Apartment], Some("An apartment deleted")), Unauthorized, NotFound),
      docs = EndpointDocs().withDescription(Some("An endpoint responsible for deleting apartment by id"))
    )

}
