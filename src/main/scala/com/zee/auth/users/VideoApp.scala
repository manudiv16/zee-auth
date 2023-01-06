package com.zee.auth.users

import zio.http._
import zio._
import zio.http.model.{Method, Status}
import zio.json._

import java.util.UUID
import nl.vroste.zio.amqp.{Channel, Amqp}
import nl.vroste.zio.amqp.model._

import java.net.URI

/** An http app that:
  *   - Accepts a `Request` and returns a `Response`
  *   - May fail with type of `Throwable`
  *   - Uses a `VideoRepo` as the environment
  */
object AccountApp {

  val channel: ZIO[Scope, Throwable, Channel] = for {
    connection <- Amqp
      .connect(AMQPConfig.default)
      .debug
    channel <- Amqp.createChannel(connection)
  } yield channel
  val hola = "kdkd"

  def apply(): Http[AccountRepo, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      // POST /videos/:name
      case Method.POST -> !! / "videos" / name / password / email =>
        ZIO.scoped {
          for {
            _ <- ZIO.logInfo("Attempting to log in")
            chann <- channel
            registered <- AccountRepo
              .register(name, password, email)
              .map(uuiid => Response.text(uuiid))
            hoa <- chann.publish(ExchangeName("test"), hola.getBytes)
          } yield (registered)
        }

      case Method.GET -> !! / "videos" / "loadup" =>
        val names = List("one", "two", "three", "four")

        val out: ZIO[AccountRepo, Throwable, List[String]] =
          ZIO.foreach(names)(name =>
            AccountRepo.register(name, "1234", "manu@gmail.com")
          )

        out.map(ids => Response.text("bulk load: " + ids.mkString(",")))

    }

}
