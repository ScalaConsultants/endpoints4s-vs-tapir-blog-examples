package io.scalac.lab.api.storage

import java.util.concurrent.atomic.AtomicLong

import io.scalac.lab.api.model.Apartment

import scala.concurrent.Future

/**
  * An implementation of apartments storage which holds all of the data in memory.
  * It accepts function to initial state from outside. By default initial state is empty.
  *
  * Note that in case of `get`, `find` and `delete` methods, `Left` with error message can be returned.
 **/
class InMemoryApartmentsStorage(initState: () => List[Apartment] = () => List.empty) extends ApartmentsStorage {
  private val storageId = new AtomicLong()
  private var storage: List[Apartment] = initState()

  override def list(from: Int, limit: Option[Int]): Future[Either[String, List[Apartment]]] =
    Future.successful(Right(storage.slice(from, from + limit.getOrElse(storage.length))))

  override def get(id: Int): Future[Either[String, Apartment]] =
    storage.find(_.id.contains(id)) match {
      case Some(value) => Future.successful(Right(value))
      case None        => Future.successful(Left(s"Apartment with id: $id not found!"))
    }

  override def find(city: String, street: String, number: String): Future[Either[String, Apartment]] = {
    storage.find { s =>
      s.address.city.equalsIgnoreCase(city) &&
      s.address.street.equalsIgnoreCase(street) &&
      s.address.number.equalsIgnoreCase(number)
    } match {
      case Some(value) => Future.successful(Right(value))
      case None        => Future.successful(Left(s"Apartment for city: $city, street: $street, number: $number not found!"))
    }
  }

  override def save(apartment: Apartment): Future[Either[String, Apartment]] = {
    val apartmentWithId = apartment.copy(id = Some(storageId.getAndIncrement()))

    storage = storage :+ apartmentWithId
    Future.successful(Right(apartmentWithId))
  }

  override def delete(id: Int): Future[Either[String, Apartment]] = {
    storage.find(_.id.contains(id)) match {
      case Some(value) =>
        storage = storage.filterNot(_.id.contains(id))
        Future.successful(Right(value))
      case None =>
        Future.successful(Left(s"Apartment with id: $id not found!"))
    }
  }
}
