package com.zee.auth.auth

import zio.*

trait JwtRepo:
  def encode(username: String): ZIO[JwtRepo, Throwable, String]
  def decode(
      token: String,
      username: String
  ): ZIO[JwtRepo, Throwable, Option[String]]

object JwtRepo:
  def encode(username: String): ZIO[JwtRepo, Throwable, String] =
    ZIO.serviceWithZIO[JwtRepo](_.encode(username))

  def decode(
      token: String,
      username: String
  ): ZIO[JwtRepo, Throwable, Option[String]] =
    ZIO.serviceWithZIO[JwtRepo](_.decode(token, username))
