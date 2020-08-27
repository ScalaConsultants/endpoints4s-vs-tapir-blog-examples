package io.scalac.lab.api.tapir

import io.scalac.lab.api.model.ApiError
import io.scalac.lab.api.security.Security.ApiKey
import sttp.tapir.EndpointOutput.StatusMapping
import sttp.tapir._
import sttp.tapir.server._

trait SecuritySupport[F[_]] extends CustomStatusMappings {

  /**
    * Defines authentication method.
    *
    * Returns either String with error message in case of failure or Api Key if authenticated successfully
   **/
  def authenticate(token: Option[String]): F[Either[ApiError, ApiKey]]

  /** Definition of the partial server endpoint which adds authentication to the endpoint */
  val secured: PartialServerEndpoint[ApiKey, Unit, ApiError, Unit, Nothing, F] = securedWithStatuses()

  /** Definition of the partial server endpoint which adds authentication to the endpoint and custom status codes */
  def securedWithStatuses(statuses: StatusMapping[_ <: ApiError]*): PartialServerEndpoint[ApiKey, Unit, ApiError, Unit, Nothing, F] =
    endpoint
      .in(auth.apiKey(header[Option[String]]("api-key")))
      .errorOut(oneOf[ApiError](Unauthorized, statuses: _*))
      .serverLogicForCurrent(authenticate)
}
