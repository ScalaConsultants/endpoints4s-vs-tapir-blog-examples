package io.scalac.lab.api.endpoints4s

import endpoints4s.Invalid
import endpoints4s.algebra.{EndpointsWithCustomErrors, JsonEntitiesFromSchemas}
import endpoints4s.generic.JsonSchemas
import io.scalac.lab.api.model.ApiError._

/**
  * Introduces custom status codes handling for responses.
  *
  * We would like to handle following one or more status codes per response with custom entity:
  * - 400 `BadRequest` with `BadRequestError` as json entity
  * - 401 `Unauthorized` with `UnauthorizedError` as json entity
  * - 404 `NotFound` with `NotFoundError` as json entity
  * */
trait CustomStatusCodes extends EndpointsWithCustomErrors with JsonEntitiesFromSchemas with JsonSchemas {

  private implicit val badRequestSchema: JsonSchema[BadRequestError] = genericJsonSchema
  private implicit val unauthorizedSchema: JsonSchema[UnauthorizedError] = genericJsonSchema
  private implicit val notFoundSchema: JsonSchema[NotFoundError] = genericJsonSchema

  // Tell endpoints4s that client errors are modeled with our custom type, BadRequestError
  type ClientErrors = BadRequestError

  // Conversion from endpoints4s internal representation of client errors to our custom type
  def invalidToClientErrors(invalid: Invalid): BadRequestError =
    BadRequestError(invalid.errors.mkString(". "))
  // Conversion from our custom type to endpoints4s internal representation of client errors
  def clientErrorsToInvalid(clientErrors: BadRequestError): Invalid =
    Invalid(clientErrors.reason)

  def clientErrorsResponseEntity: ResponseEntity[BadRequestError] = jsonResponse[BadRequestError]

  // Override the documentation of the response for client errors
  override lazy val clientErrorsResponse: Response[BadRequestError] =
    badRequest(docs = Some("Api error returned when request could not be processed properly"))

  // We define three shorthands for our API responses, `badRequest`, `unauthorized`, and `notFound`
  val badRequest: Response[BadRequestError] = clientErrorsResponse

  val unauthorized: Response[UnauthorizedError] =
    response(Unauthorized, jsonResponse[UnauthorizedError], Some("Api error returned when request could not be authenticated"))

  val notFound: Response[NotFoundError] =
    response(NotFound, jsonResponse[NotFoundError], Some("Api error returned when entity could not be found"))

  // We use the same representation as endpoints4s for server errors
  type ServerError = Throwable
  def throwableToServerError(throwable: Throwable): Throwable = throwable
  def serverErrorToThrowable(serverError: Throwable): Throwable = serverError
  def serverErrorResponseEntity: ResponseEntity[Throwable] =
    jsonResponse(field[String]("reason")
      .xmap[Throwable](new Exception(_))(_.toString))

}
