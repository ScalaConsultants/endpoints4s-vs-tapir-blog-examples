package io.scalac.lab.api.storage

import io.scalac.lab.api.model.Apartment
import io.scalac.lab.api.model.ApiError.{BadRequestError, NotFoundError}

import scala.concurrent.Future

// Similar to `ApartmentsStorage`, but uses precise return
// types instead of `ApiError` everywhere
trait NarrowerApartmentsStorage {
  def list(from: Int, limit: Option[Int]): Future[Either[BadRequestError, List[Apartment]]]
  def get(id: Int): Future[Either[NotFoundError, Apartment]]
  def find(city: String, street: String, number: String): Future[Either[Either[BadRequestError, NotFoundError], Apartment]]
  def save(a: Apartment): Future[Either[BadRequestError, Apartment]]
  def delete(id: Int): Future[Either[NotFoundError, Apartment]]
}
