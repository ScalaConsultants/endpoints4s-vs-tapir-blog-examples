package io.scalac.lab.api.tapir

import io.scalac.lab.api.security.Security.ApiKey
import sttp.model.StatusCode.Unauthorized
import sttp.tapir._
import sttp.tapir.server._

trait SecuritySupport[F[_]] {

  /**
    * Defines authentication method.
    *
    * Returns either String with error message in case of failure or Api Key if authenticated successfully
   **/
  def authenticate(token: Option[String]): F[Either[String, ApiKey]]

  /** * Definition of the partial server endpoint which adds authentication to the endpoint */
  val securedEndpoint: PartialServerEndpoint[ApiKey, Unit, String, Unit, Nothing, F] = endpoint
    .in(auth.apiKey(header[Option[String]]("api-key")))
    .errorOut(statusCode(Unauthorized).and(stringBody.description("An error message when authentication failed")))
    .serverLogicForCurrent(authenticate)

}
