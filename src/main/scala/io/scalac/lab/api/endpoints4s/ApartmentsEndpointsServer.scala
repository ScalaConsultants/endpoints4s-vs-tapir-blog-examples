package io.scalac.lab.api.endpoints4s

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive1, Route}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import endpoints4s.Tupler
import endpoints4s.akkahttp.server.{BuiltInErrors, Endpoints, JsonEntitiesFromSchemas}
import io.circe.generic.auto._
import io.scalac.lab.api.model.ApiError
import io.scalac.lab.api.security.Security.ApiKey
import io.scalac.lab.api.security.SecurityService
import io.scalac.lab.api.storage.ApartmentsStorage

import scala.util.Success

class ApartmentsEndpointsServer(storage: ApartmentsStorage, security: SecurityService)
    extends ApartmentsEndpointsDefinition
    with Endpoints
    with BuiltInErrors
    with JsonEntitiesFromSchemas
    with FailFastCirceSupport {

  val listApartmentsRoute: Route = listApartments.implementedByAsync { case (paging, _) => storage.list(paging.from, paging.limit) }

  val getApartmentRoute: Route = getApartment.implementedByAsync { case (id, _) => storage.get(id) }

  val findApartmentRoute: Route = findApartment.implementedByAsync {
    case (address, _) => storage.find(address.city, address.street, address.number)
  }

  val addApartmentRoute: Route = addApartment.implementedByAsync { case (apartment, _) => storage.save(apartment) }

  val deleteApartmentRoute: Route = deleteApartment.implementedByAsync { case (id, _) => storage.delete(id) }

  override def authenticatedRequest[A, B](request: Directive1[A])(implicit tupler: Tupler.Aux[A, ApiKey, B]): Directive1[B] = {
    optionalHeaderValueByName("api-key").flatMap { authToken: Option[String] =>
      onComplete(security.authenticate(authToken)).flatMap {
        case Success(Right(apiKey)) => request.map(r => tupler(r, apiKey))
        case _                      => complete((Unauthorized, authToken.fold("Missing API key")(_ => "Incorrect API key")))
      }
    }
  }

  override def withStatusCodes[A](response: A => Route, codes: StatusCode*): Either[ApiError, A] => Route = {
    case Left(x @ ApiError.UnauthorizedError(_)) => complete(Unauthorized, x)
    case Left(x @ ApiError.NotFoundError(_))     => complete(NotFound, x)
    case Left(x @ ApiError.BadRequestError(_))   => complete(BadRequest, x)
    case Right(value)                            => response(value)
  }
}
