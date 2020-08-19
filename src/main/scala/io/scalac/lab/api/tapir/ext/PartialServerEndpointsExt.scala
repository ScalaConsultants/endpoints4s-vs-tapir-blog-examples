package io.scalac.lab.api.tapir.ext

import sttp.tapir.docs.openapi.{EndpointToOpenAPIDocs, OpenAPIDocsOptions}
import sttp.tapir.openapi.{Info, OpenAPI}
import sttp.tapir.server.PartialServerEndpoint

/**
  * Trait that extends creation of `OpenAPI` documentation for `PartialServerEndpoint`,
  * which is not yet provided by default in Tapir.
  */
trait PartialServerEndpointsExt {

  implicit class RichOpenAPIPartialServerEndpoints[F[_]](serverEndpoints: Iterable[PartialServerEndpoint[_, _, _, _, _, F]]) {
    def toOpenAPI(title: String, version: String)(implicit options: OpenAPIDocsOptions): OpenAPI = toOpenAPI(Info(title, version))

    def toOpenAPI(info: Info)(implicit options: OpenAPIDocsOptions): OpenAPI =
      EndpointToOpenAPIDocs.toOpenAPI(info, serverEndpoints.map(_.endpoint), options)
  }

}

object PartialServerEndpointsExt extends PartialServerEndpointsExt
