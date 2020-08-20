package io.scalac.lab.api.endpoints4s

import endpoints4s.Tupler
import endpoints4s.algebra.Endpoints
import io.scalac.lab.api.security.Security.ApiKey

trait SecuritySupport extends Endpoints {

  /** A request which is rejected by the server if it does not contain a valid authentication token */
  def authenticatedRequest[I, O](request: Request[I])(implicit tupler: Tupler.Aux[I, ApiKey, O]): Request[O]

  /**
    * User-facing constructor for endpoints requiring authentication.
    *
    * Returns an endpoint requiring authentication information to be provided in the specific request header.
    * It returns `response` if the request is correctly authenticated, otherwise it returns an empty `Unauthorized` response.
    */
  def authenticatedEndpoint[U, O, I](request: Request[U], response: Response[O], docs: EndpointDocs = EndpointDocs())(
      implicit tupler: Tupler.Aux[U, ApiKey, I]): Endpoint[I, O] =
    endpoint(
      authenticatedRequest(request),
      response,
      docs
    )

}
