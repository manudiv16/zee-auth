package com.zee.auth

import auth._
import healthcheck.HealthcheckApp
import zio.http._
import zio._
import zio.http.{Http, Middleware, Request, Response, Server}
import zio.http.middleware.HttpMiddleware
import zio.http.model.Status
import zio.logging.{LogFormat, console}
import zio.Console

import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Main extends ZIOAppDefault:
  val errorMiddleware = new HttpMiddleware[Any, Throwable] {

    override def apply[R1 <: Any, E1 >: Throwable](
        http: Http[R1, E1, Request, Response]
    )(implicit trace: Trace): Http[R1, E1, Request, Response] =
      http.catchAll { ex =>
        val zio: ZIO[Any, IOException, Response] = for {
          _ <- ZIO.logError(ex.toString)
        } yield Response.status(Status.InternalServerError)
        Http.responseZIO(zio)
      }
  }

  val middlewares = errorMiddleware

  val logger =
    Runtime.removeDefaultLoggers >>> console(LogFormat.colored)

  val requestMiddleWare = Middleware
    .identity[Request, Response]
    .contramap[Request](
      _.addHeader(
        "Seen",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now())
      )
    )

  val config = ServerConfig.default.port(49192)
  val configLayer = ServerConfig.live(config)

  override lazy val run = for {
    _ <- ZIO.logInfo("Starting up").provide(logger)
    serverFibre <- Server
      .serve(
        CookieAuthApp() @@ requestMiddleWare @@ middlewares ++ HealthcheckApp()
      )
      .provide(
        Server.live,
        ZLayer.succeed(Console.ConsoleLive),
        configLayer,
        JwtImpl.live,
        logger
      )
      .fork
    _ <- Console.readLine("Press enter to stop the server\n")
    _ <- Console.printLine("Interrupting server")
    _ <- serverFibre.interrupt
  } yield ()
