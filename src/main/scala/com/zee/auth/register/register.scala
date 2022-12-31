package com.zee.auth.register

import zio._
import zio.http._
import java.time.Clock
import com.zee.auth.auth._
import java.lang.System.Logger
import zio.http.model.{Cookie, Method, Status}

object CookieAuthApp:

  // Login is successful only if the password is the reverse of the username
  lazy val live: Http[JwtRepo, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      case Method.POST -> !! / "register" / username / password =>
        for {
          _ <- ZIO.logInfo("Attempting to log in")
          a <- JwtRepo.encode(username)
        } yield
          if (password.reverse == username)
            Response
              .text("Logged in")
              .addCookie(
                Cookie("jwt", a).withPath(!!)
              )
          else
            Response
              .text("Invalid username or password.")
              .setStatus(Status.Unauthorized)
    }
