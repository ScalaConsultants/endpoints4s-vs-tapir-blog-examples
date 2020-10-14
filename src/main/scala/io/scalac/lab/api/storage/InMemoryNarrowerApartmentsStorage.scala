package io.scalac.lab.api.storage
import java.util.concurrent.atomic.AtomicLong

import io.scalac.lab.api.model.Apartment
import io.scalac.lab.api.model.ApiError.{BadRequestError, NotFoundError}

import scala.concurrent.Future

// Exactly the same implementation as `InMemoryApartmentsStorage`, except it
// extends `NarrowerApartmentsStorage`.
class InMemoryNarrowerApartmentsStorage(initState: () => List[Apartment] = () => List.empty) extends NarrowerApartmentsStorage {
  private val storageId = new AtomicLong()
  private var storage: List[Apartment] = initState()

  override def list(from: Int, limit: Option[Int]): Future[Either[BadRequestError, List[Apartment]]] =
    Future.successful(Right(storage.slice(from, from + limit.getOrElse(storage.length))))

  override def get(id: Int): Future[Either[NotFoundError, Apartment]] =
    storage.find(_.id.contains(id)) match {
      case Some(value) => Future.successful(Right(value))
      case None        => Future.successful(Left(NotFoundError(s"Apartment with id: $id not found!")))
    }

  override def find(city: String, street: String, number: String): Future[Either[Either[BadRequestError, NotFoundError], Apartment]] = {
    storage.find { s =>
      s.address.city.equalsIgnoreCase(city) &&
        s.address.street.equalsIgnoreCase(street) &&
        s.address.number.equalsIgnoreCase(number)
    } match {
      case Some(value) => Future.successful(Right(value))
      case None =>
        Future.successful(Left(Right(NotFoundError(s"Apartment for city: $city, street: $street, number: $number not found!"))))
    }
  }

  override def save(apartment: Apartment): Future[Either[BadRequestError, Apartment]] = {
    val apartmentWithId = apartment.copy(id = Some(storageId.getAndIncrement()))

    storage = storage :+ apartmentWithId
    Future.successful(Right(apartmentWithId))
  }

  override def delete(id: Int): Future[Either[NotFoundError, Apartment]] = {
    storage.find(_.id.contains(id)) match {
      case Some(value) =>
        storage = storage.filterNot(_.id.contains(id))
        Future.successful(Right(value))
      case None =>
        Future.successful(Left(NotFoundError(s"Apartment with id: $id not found!")))
    }
  }
}
