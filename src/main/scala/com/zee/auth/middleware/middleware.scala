package com.zee.auth.middleware
import zio._
import zio.http.{Http, Middleware, Request, Response, Server}
import zio.http.middleware.HttpMiddleware
import java.io.IOException
import zio.http.model.Status
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

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

val requestMiddleWare = Middleware
  .identity[Request, Response]
  .contramap[Request](
    _.addHeader(
      "Seen",
      DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now())
    )
  )
