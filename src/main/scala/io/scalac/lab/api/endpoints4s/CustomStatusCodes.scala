package io.scalac.lab.api.endpoints4s

import endpoints4s.algebra.{Endpoints, JsonEntitiesFromSchemas}
import endpoints4s.generic.JsonSchemas
import io.scalac.lab.api.model.ApiError
import io.scalac.lab.api.model.ApiError._

/**
  * Introduces custom status codes handling for responses.
  *
  * We would like to handle following one or more status codes per response with custom entity:
  * - 400 `BadRequest` with `BadRequestError` as json entity
  * - 401 `Unauthorized` with `UnauthorizedError` as json entity
  * - 404 `NotFound` with `NotFoundError` as json entity
  * */
trait CustomStatusCodes extends Endpoints with JsonEntitiesFromSchemas with JsonSchemas {

  private implicit val badRequestSchema: JsonSchema[BadRequestError] = genericJsonSchema
  private implicit val unauthorizedSchema: JsonSchema[UnauthorizedError] = genericJsonSchema
  private implicit val notFoundSchema: JsonSchema[NotFoundError] = genericJsonSchema

  val badRequest: Response[BadRequestError] =
    response(BadRequest, jsonResponse[BadRequestError], Some("Api error returned when request could not be processed properly"))

  val unauthorized: Response[UnauthorizedError] =
    response(Unauthorized, jsonResponse[UnauthorizedError], Some("Api error returned when request could not be authenticated"))

  val notFound: Response[NotFoundError] =
    response(NotFound, jsonResponse[NotFoundError], Some("Api error returned when entity could not be found"))

  /**
    * Uses given status codes to handle or create documentation based on that.
    * Note that single response may have more than one status code.
   **/
  def withStatusCodes[A](response: Response[A], codes: StatusCode*): Response[Either[ApiError, A]]

}
