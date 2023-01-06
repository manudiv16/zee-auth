package com.zee.auth.users

import zio._
import zio.stream.ZStream

trait AccountRepo {
  def register(
      username: String,
      password: String,
      email: String
  ): Task[String]
}

object AccountRepo {
  def register(
      username: String,
      password: String,
      email: String
  ): ZIO[AccountRepo, Throwable, String] =
    ZIO.serviceWithZIO[AccountRepo](_.register(username, password, email))
}
