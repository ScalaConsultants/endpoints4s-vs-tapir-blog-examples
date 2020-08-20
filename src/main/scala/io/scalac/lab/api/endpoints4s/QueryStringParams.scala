package io.scalac.lab.api.endpoints4s

import endpoints4s.Validated
import endpoints4s.algebra.Endpoints
import io.scalac.lab.api.model.{Address, Paging}

/**
  * Defines query string parameters for supporting apartment endpoints.
  *
  * Apartment endpoints requires:
  * - paging query string with from and limit
  * - address query string with city, street and street number
  *
  * Additionally following validation will be applied:
  * - paging.from needs to be above or equal to 0
  * - paging.limit needs to be between 0 and 100 (inclusive) if provided
  *
  * - address.city cannot be empty
  * - address.street cannot be empty
  * - address.number cannot be empty and needs to contain at least one digit
 **/
trait QueryStringParams extends Endpoints {

  private val pagingFrom = qs[Int]("from", Some("Indicates where we should start returning data from"))
  private val pagingLimit = qs[Option[Int]]("limit", Some("An optional number of rows to be returned"))

  private val addressCity = qs[String]("city", Some("A city we want to find apartment for"))
  private val addressStreet = qs[String]("street", Some("A street we want to find apartment for"))
  private val addressStreetNumber = qs[String]("number", Some("A street number we want to find apartment for"))

  val pagingQueryString: QueryString[Paging] =
    (pagingFrom & pagingLimit).xmapPartial(toValidatedPaging)(x => (x.from, x.limit))

  val addressQueryString: QueryString[Address] =
    (addressCity & addressStreet & addressStreetNumber).xmapPartial(toValidatedAddress)(a => (a.city, a.street, a.number))

  private def toValidatedAddress(x: (String, String, String)): Validated[Address] = x match {
    case (city, street, number) =>
      val result = for {
        validCity <- validateCity(city)
        validStreet <- validateStreet(street)
        validNumber <- validateStreetNumber(number)
      } yield Address(validCity, validStreet, validNumber)

      Validated.fromEither(result.left.map(Seq(_)))
  }

  private def validateCity(city: String): Either[String, String] =
    Either.cond(city.isEmpty, city, "Address.city cannot be empty")

  private def validateStreet(street: String): Either[String, String] =
    Either.cond(street.isEmpty, street, "Address.street cannot be empty")

  private def validateStreetNumber(number: String): Either[String, String] =
    Either.cond(number.exists(_.isDigit), number, "Address.number cannot be empty and needs to contain at least one digit")

  private def toValidatedPaging(x: (Int, Option[Int])): Validated[Paging] = x match {
    case (from, limit) =>
      val result = for {
        validPagingFrom <- validatePagingFrom(from)
        validPagingLimit <- validatePagingLimit(limit)
      } yield Paging(validPagingFrom, validPagingLimit)

      Validated.fromEither(result.left.map(Seq(_)))
  }

  private def validatePagingFrom(from: Int): Either[String, Int] =
    Either.cond(from >= 0, from, "Paging.from cannot be lower than 0")

  private def validatePagingLimit(limit: Option[Int]): Either[String, Option[Int]] =
    Either.cond(limit.forall(l => 0 <= l && l <= 100), limit, "Paging.limit needs to be between 0 and 100 (both inclusive)")

}
