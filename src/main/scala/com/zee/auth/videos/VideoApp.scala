package com.zee.auth.videos

import zio.http._
import zio._
import zio.http.model.{Method, Status}
import zio.json._

import java.util.UUID

/** An http app that:
  *   - Accepts a `Request` and returns a `Response`
  *   - May fail with type of `Throwable`
  *   - Uses a `VideoRepo` as the environment
  */
object VideoApp {
  def apply(): Http[VideoRepo, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      // POST /videos/:name
      case Method.POST -> !! / "videos" / name / password / email =>
        VideoRepo
          .register(name, password, email)
          .map(uuiid => Response.text(uuiid))

      case Method.GET -> !! / "videos" / "loadup" =>
        val names = List("one", "two", "three", "four")

        val out: ZIO[VideoRepo, Throwable, List[String]] =
          ZIO.foreach(names)(name =>
            VideoRepo.register(name, "1234", "manu@gmail.com")
          )

        out.map(ids => Response.text("bulk load: " + ids.mkString(",")))

    }

}
