package io.scalac.lab.api.model

sealed trait ApiError

object ApiError {

  case class UnauthorizedError(reason: String) extends ApiError

  case class NotFoundError(message: String) extends ApiError

  case class BadRequestError(reason: String) extends ApiError

}
