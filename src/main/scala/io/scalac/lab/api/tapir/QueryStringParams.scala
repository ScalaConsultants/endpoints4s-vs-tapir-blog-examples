package io.scalac.lab.api.tapir

import io.scalac.lab.api.model.{Address, Paging}
import sttp.tapir.Validator._
import sttp.tapir._

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
trait QueryStringParams {

  private val pagingFrom = query[Int]("from")
    .description("Indicates where we should start returning data from")
    .example(1)

  private val pagingLimit = query[Option[Int]]("limit")
    .description("An optional number of rows to be returned")
    .example(Some(100))

  private val addressCity = query[String]("city")
    .description("A city we want to find apartment for")
    .example("Warsaw")
  private val addressStreet = query[String]("street")
    .description("A street we want to find apartment for")
    .example("Pulawska")
  private val addressStreetNumber = query[String]("number")
    .description("A street number we want to find apartment for")
    .example("10A")

  val pagingIn: EndpointInput[Paging] =
    (pagingFrom / pagingLimit).mapTo(Paging).validate(pagingValidator)

  val addressIn: EndpointInput[Address] =
    (addressCity / addressStreet / addressStreetNumber).mapTo(Address).validate(addressValidator)

  private def pagingValidator: Validator[Paging] =
    Validator.all[Paging](
      greater(0, "from").contramap(_.from),
      max[Int](100).asOptionElement.contramap(_.limit),
      min[Int](0).asOptionElement.contramap(_.limit)
    )

  private def addressValidator: Validator[Address] =
    Validator.all[Address](
      nonEmpty("city").contramap(_.city),
      nonEmpty("street").contramap(_.street),
      custom[String](_.exists(_.isDigit), "Street number cannot be empty and needs to contain at least one digit").contramap(_.number)
    )

  /** Custom validator which checks if value is greater (or equal) to specific number */
  private def greater(n: Int, field: String): Validator[Int] =
    custom[Int](x => x >= n, s"Field: $field needs to be greater than $n")

  /** Custom validator, which checks if string is non empty */
  private def nonEmpty(field: String): Validator[String] =
    custom[String](_.nonEmpty, s"Field: $field cannot be empty")

}

object QueryStringParams extends QueryStringParams
