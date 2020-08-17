package io.scalac.lab.api.endpoints4s

import akka.http.scaladsl.server.Route
import endpoints4s.akkahttp.server.{BuiltInErrors, Endpoints, JsonEntitiesFromSchemas}
import io.scalac.lab.api.storage.ApartmentsStorage

class ApartmentsEndpointsServer(storage: ApartmentsStorage)
    extends ApartmentsEndpointsDefinition
    with Endpoints
    with BuiltInErrors
    with JsonEntitiesFromSchemas {

  val listApartmentsRoute: Route = listApartments.implementedByAsync(_ => storage.list())

  val getApartmentRoute: Route = getApartment.implementedByAsync(id => storage.get(id))

  val findApartmentRoute: Route = findApartment.implementedByAsync { case (city, street, number) => storage.find(city, street, number) }

  val addApartmentRoute: Route = addApartment.implementedByAsync(apartment => storage.save(apartment))

  val deleteApartmentRoute: Route = deleteApartment.implementedByAsync(id => storage.delete(id))

}
