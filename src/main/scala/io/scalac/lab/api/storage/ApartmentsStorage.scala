package io.scalac.lab.api.storage

import io.scalac.lab.api.model.{Apartment, ApiError}

import scala.concurrent.Future

trait ApartmentsStorage {
  def list(from: Int, limit: Option[Int]): Future[Either[ApiError, List[Apartment]]]
  def get(id: Int): Future[Either[ApiError, Apartment]]
  def find(city: String, street: String, number: String): Future[Either[ApiError, Apartment]]
  def save(a: Apartment): Future[Either[ApiError, Apartment]]
  def delete(id: Int): Future[Either[ApiError, Apartment]]
}
