package io.scalac.lab.api.tapir

import akka.http.scaladsl.server.Route
import io.scalac.lab.api.model.ApiError
import io.scalac.lab.api.security.Security.ApiKey
import io.scalac.lab.api.security.SecurityService
import io.scalac.lab.api.storage.ApartmentsStorage
import sttp.tapir.server.akkahttp._

import scala.concurrent.Future

/**
  * This class shows that Tapir does not require any additional implementation
  * other than what we have in definition and we can add our logic directly
 **/
class ApartmentsEndpointsServer(storage: ApartmentsStorage, security: SecurityService) extends ApartmentsEndpointsDefinition[Future] {

  val listApartmentsRoute: Route = listApartments.serverLogic { case (_, paging) => storage.list(paging.from, paging.limit) }.toRoute

  val getApartmentRoute: Route = getApartment.serverLogic { case (_, id) => storage.get(id) }.toRoute

  val findApartmentRoute: Route = findApartment.serverLogic {
    case (_, address) => storage.find(address.city, address.street, address.number)
  }.toRoute

  val addApartmentRoute: Route = addApartment.serverLogic { case (_, apartment) => storage.save(apartment) }.toRoute

  val deleteApartmentRoute: Route = deleteApartment.serverLogic { case (_, id) => storage.delete(id) }.toRoute

  override def authenticate(token: Option[String]): Future[Either[ApiError, ApiKey]] = security.authenticate(token)

}
