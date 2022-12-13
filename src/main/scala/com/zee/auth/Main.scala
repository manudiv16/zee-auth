package com.zee.auth

import auth.{JwtImpl, CookieAuthApp}
import healthcheck.HealthcheckApp

import zio.{ZIOAppDefault, ZIO, Console}
import zio.http.Server
import middleware.{errorMiddleware, requestMiddleWare}
import logger.logger
import serverConfig.configLayer

object Main extends ZIOAppDefault:

  override lazy val run = for {
    _ <- ZIO.logInfo("Starting up").provide(logger)
    serverFibre <- Server
      .serve(
        CookieAuthApp.live ++
          HealthcheckApp.live @@
          errorMiddleware @@
          requestMiddleWare
      )
      .provide(
        Server.live,
        configLayer,
        JwtImpl.live
      )
      .provide(logger)
      .fork
    _ <- Console.readLine("Press enter to stop the server\n")
    _ <- Console.printLine("Interrupting server")
    _ <- serverFibre.interrupt
  } yield ()
