package com.zee.auth.healthcheck

import zio._
import zio.http._
import zio.http.model.Method

/** An http app that:
  *   - Accepts a `Request` and returns a `Response`
  *   - Does not fail
  *   - Uses a String for the env, for the webapp root
  */
object HealthcheckApp:
  lazy val live: Http[Any, Nothing, Request, Response] =
    Http.collect[Request] { case Method.GET -> !! / "healthz" =>
      Response.text(s"UP!")
    }
