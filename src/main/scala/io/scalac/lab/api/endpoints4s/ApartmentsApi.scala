package io.scalac.lab.api.endpoints4s

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import endpoints4s.akkahttp.server
import endpoints4s.openapi
import endpoints4s.openapi.model.{Info, OpenApi}
import io.scalac.lab.api.storage.InMemoryApartmentsStorage
import io.scalac.lab.api.tapir.ApartmentsEndpointsServer

import scala.concurrent.ExecutionContext
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
  private val api = new ApartmentsEndpointsServer(apiStorage)
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
