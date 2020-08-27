package io.scalac.lab.api.endpoints4s

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import endpoints4s.akkahttp.server
import endpoints4s.openapi.model.{Info, OpenApi, SecurityRequirement, SecurityScheme}
import endpoints4s.{Tupler, openapi}
import io.scalac.lab.api.model.ApiError
import io.scalac.lab.api.model.ApiError.UnauthorizedError
import io.scalac.lab.api.security.Security.ApiKey
import io.scalac.lab.api.security.SecurityService
import io.scalac.lab.api.storage.InMemoryApartmentsStorage
import io.scalac.lab.api.tapir.ApartmentsEndpointsServer

import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object ApartmentsApi extends App {

  private implicit val actorSystem: ActorSystem = ActorSystem()
  private implicit val dispatcher: ExecutionContext = actorSystem.dispatcher

  private object DocumentedEndpoints
      extends ApartmentsEndpointsDefinition
      with openapi.Endpoints
      with openapi.JsonEntitiesFromSchemas
      with openapi.JsonSchemas {

    lazy val api: OpenApi = openApi(Info("The Apartments API", "1.0.0"))(
      listApartments,
      getApartment,
      findApartment,
      addApartment,
      deleteApartment
    )

    override def authenticatedRequest[I, O](request: DocumentedRequest)(implicit tupler: Tupler.Aux[I, ApiKey, O]): DocumentedRequest =
      request

    // To make Swagger UI work with Authorize Button, we need to add Security Requirement,
    // because Endpoints4s is not going to do that for us.
    override def authenticatedEndpoint[U, O, I](request: DocumentedRequest, response: List[DocumentedResponse], docs: EndpointDocs)(
        implicit
        tupler: Tupler.Aux[U, ApiKey, I]): DocumentedEndpoint =
      super
        .authenticatedEndpoint(request, response, docs)
        .withSecurity(SecurityRequirement("apiKeyAuth", SecurityScheme("apiKey", None, Some("api-key"), Some("header"), None, None)))

    override def withStatusCodes[A](responses: List[DocumentedResponse], codes: Int*): List[DocumentedResponse] = {
      responses :++ codes.flatMap {
        case 400 => badRequest
        case 401 => unauthorized
        case 404 => notFound
      }
    }

    // We are going to return empty responses here for errors,
    // because we want to customize status codes for each endpoint independently
    override lazy val clientErrorsResponse: List[DocumentedResponse] = List.empty
    override lazy val serverErrorResponse: List[DocumentedResponse] = List.empty

  }

  private object DocumentationServer extends server.Endpoints with server.JsonEntitiesFromEncodersAndDecoders {

    private val openApiRoute: Route =
      endpoint(get(path / "openapi.json"), ok(jsonResponse[OpenApi]))
        .implementedBy(_ => DocumentedEndpoints.api)

    lazy val docs: Route = concat(
      pathPrefix("docs") {
        pathEndOrSingleSlash {
          getFromResource("web/index.html")
        }
      },
      pathPrefix("swagger-ui") {
        getFromResourceDirectory("web/swagger-ui/")
      },
      openApiRoute
    )
  }

  private val apiStorage = new InMemoryApartmentsStorage()
  private val apiSecurity = new SecurityService {
    override def authenticate(token: Option[String]): Future[Either[ApiError, ApiKey]] =
      token match {
        case Some(value) if value == "admin" => Future.successful(Right(ApiKey(value)))
        case _                               => Future.successful(Left(UnauthorizedError("Authentication failed!")))
      }
  }
  private val api = new ApartmentsEndpointsServer(apiStorage, apiSecurity)
  private val apiRoutes =
    concat(
      api.listApartmentsRoute,
      api.findApartmentRoute,
      api.getApartmentRoute,
      api.addApartmentRoute,
      api.deleteApartmentRoute,
      DocumentationServer.docs
    )

  val bindingFuture = Http().newServerAt("localhost", 8080).bind(apiRoutes)

  println("Go to: http://localhost:8080/docs")
  println("Press any key to exit ...")
  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => actorSystem.terminate())

}
