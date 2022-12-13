package com.zee.auth.auth
import zio._
import zio.http._
import zio.http.model.{Cookie, Method, Status}
import java.time.Clock
import com.zee.auth.auth._
import java.lang.System.Logger

object CookieAuthApp:

  // Login is successful only if the password is the reverse of the username
  def login: Http[JwtRepo, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      case Method.GET -> !! / "login" / username / password =>
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

  def apply() =
    login
