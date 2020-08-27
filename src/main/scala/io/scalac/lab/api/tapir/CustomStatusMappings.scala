package io.scalac.lab.api.tapir

import cats.syntax.functor._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import io.scalac.lab.api.model.ApiError
import io.scalac.lab.api.model.ApiError._
import sttp.model.{StatusCode => Code}
import sttp.tapir.EndpointOutput.StatusMapping
import sttp.tapir._
import sttp.tapir.json.circe._

/**
  * Introduces custom status codes handling for responses.
  *
  * We would like to handle following one or more status codes per response with custom entity:
  * - 400 `BadRequest` with `BadRequestError` as json entity
  * - 401 `Unauthorized` with `UnauthorizedError` as json entity
  * - 404 `NotFound` with `NotFoundError` as json entity
  * */
trait CustomStatusMappings {

  private implicit val encodeApiError: Encoder[ApiError] = Encoder.instance {
    case x @ UnauthorizedError(_) => x.asJson
    case x @ BadRequestError(_)   => x.asJson
    case x @ NotFoundError(_)     => x.asJson
  }

  private implicit val decodeApiError: Decoder[ApiError] =
    List[Decoder[ApiError]](
      Decoder[UnauthorizedError].widen,
      Decoder[BadRequestError].widen,
      Decoder[NotFoundError].widen
    ).reduceLeft(_ or _)

  val Unauthorized: StatusMapping[UnauthorizedError] =
    statusMapping(Code.Unauthorized, jsonBody[UnauthorizedError].description("Api error returned when request could not be authenticated"))

  val BadRequest: StatusMapping[BadRequestError] =
    statusMapping(Code.BadRequest, jsonBody[BadRequestError].description("Api error returned when request could not be processed properly"))

  val NotFound: StatusMapping[NotFoundError] =
    statusMapping(Code.NotFound, jsonBody[NotFoundError].description("Api error returned when entity could not be found"))

}
