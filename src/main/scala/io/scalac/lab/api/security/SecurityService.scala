package io.scalac.lab.api.security

import io.scalac.lab.api.security.Security.ApiKey

import scala.concurrent.Future

trait SecurityService {
  def authenticate(token: Option[String]): Future[Either[String, ApiKey]]
}
