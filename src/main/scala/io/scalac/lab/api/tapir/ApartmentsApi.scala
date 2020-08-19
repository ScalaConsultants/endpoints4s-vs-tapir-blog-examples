package io.scalac.lab.api.tapir

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import io.scalac.lab.api.security.Security.ApiKey
import io.scalac.lab.api.security.SecurityService
import io.scalac.lab.api.storage.InMemoryApartmentsStorage
import io.scalac.lab.api.tapir.ext.PartialServerEndpointsExt._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.akkahttp.SwaggerAkka

import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object ApartmentsApi extends App {

  private implicit val actorSystem: ActorSystem = ActorSystem()
  private implicit val dispatcher: ExecutionContext = actorSystem.dispatcher

  private val apiStorage = new InMemoryApartmentsStorage()
  private val apiSecurity = new SecurityService {
    override def authenticate(token: Option[String]): Future[Either[String, ApiKey]] =
      token match {
        case Some(value) if value == "admin" => Future.successful(Right(ApiKey(value)))
        case _                               => Future.successful(Left("Authentication failed!"))
      }
  }
  private val api = new ApartmentsEndpointsServer(apiStorage, apiSecurity)

  private val openApiDocs: OpenAPI = List(
    api.listApartments,
    api.findApartment,
    api.getApartment,
    api.addApartment,
    api.deleteApartment
  ).toOpenAPI("The Apartments API", "1.0.0")

  private val apiRoutes =
    concat(
      api.listApartmentsRoute,
      api.findApartmentRoute,
      api.getApartmentRoute,
      api.addApartmentRoute,
      api.deleteApartmentRoute,
      new SwaggerAkka(openApiDocs.toYaml).routes
    )

  val bindingFuture = Http().newServerAt("localhost", 8090).bind(apiRoutes)

  println("Go to: http://localhost:8090/docs")
  println("Press any key to exit ...")
  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => actorSystem.terminate())

}
