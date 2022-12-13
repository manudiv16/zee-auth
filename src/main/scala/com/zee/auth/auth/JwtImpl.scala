package com.zee.auth.auth
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import zio._

import java.time.Clock
import zio.*
val SECRET_KEY = "secretKey!!"
implicit val clock: Clock = Clock.systemUTC

object JwtImpl:
  lazy val live =
    ZLayer.succeed(make)

  lazy val make: JwtRepo =
    new:
      override def encode(username: String) =
        val json = s"""{"user": "${username}"}"""
        val claim = JwtClaim {
          json
        }.issuedNow.expiresIn(300)
        ZIO.succeed(Jwt.encode(claim, SECRET_KEY, JwtAlgorithm.HS512))

      override def decode(token: String, username: String) =
        val value = for {
          a <- Jwt
            .decode(token, SECRET_KEY, Seq(JwtAlgorithm.HS512))
            .toOption
        } yield (a.content)
        ZIO.succeed(value)
