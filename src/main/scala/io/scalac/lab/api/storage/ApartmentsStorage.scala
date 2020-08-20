package io.scalac.lab.api.storage

import io.scalac.lab.api.model.Apartment

import scala.concurrent.Future

trait ApartmentsStorage {
  def list(from: Int, limit: Option[Int]): Future[Either[String, List[Apartment]]]
  def get(id: Int): Future[Either[String, Apartment]]
  def find(city: String, street: String, number: String): Future[Either[String, Apartment]]
  def save(a: Apartment): Future[Either[String, Apartment]]
  def delete(id: Int): Future[Either[String, Apartment]]
}
