package com.zee.auth

import auth.{JwtImpl, CookieAuthApp}
import healthcheck.HealthcheckApp
import zio.metrics
import zio.{ZIOAppDefault, ZIO, Console}
import zio.http.Server
import middleware.{errorMiddleware, requestMiddleWare}
import logger.logger
import serverConfig.configLayer
import com.zee.auth.users.AccountApp
import com.zee.auth.users.PersistentVideoRepo

object Main extends ZIOAppDefault:

  override lazy val run = for {
    _ <- ZIO.logInfo("Starting up").provide(logger)
    serverFibre <- Server
      .serve(
        CookieAuthApp.live ++
          AccountApp() ++
          HealthcheckApp.live @@
          errorMiddleware @@
          requestMiddleWare
      )
      .provide(
        Server.live,
        configLayer,
        JwtImpl.live,
        PersistentVideoRepo.layer
      )
      .provide(logger)
      .fork
    _ <- Console.readLine("Press enter to stop the server\n")
    _ <- Console.printLine("Interrupting server")
    _ <- serverFibre.interrupt
  } yield ()
