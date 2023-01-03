package com.zee.auth.videos

import zio._
import zio.stream.ZStream

trait VideoRepo {
  def register(
      username: String,
      password: String,
      email: String
  ): Task[String]
}

object VideoRepo {
  def register(
      username: String,
      password: String,
      email: String
  ): ZIO[VideoRepo, Throwable, String] =
    ZIO.serviceWithZIO[VideoRepo](_.register(username, password, email))
}
