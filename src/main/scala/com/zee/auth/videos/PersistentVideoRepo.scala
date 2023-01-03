package com.zee.auth.videos

import io.getquill.jdbczio.Quill
import io.getquill.{Escape, H2ZioJdbcContext}
import zio._
import zio.stream.ZStream
import io.getquill._
import io.getquill.jdbczio.Quill
import io.getquill.{Escape, PostgresZioJdbcContext}
import io.getquill.{Escape, H2ZioJdbcContext}
import zio._
import zio.stream.ZStream

import java.util.UUID
import javax.sql.DataSource

case class VideoTable(
    uuid: UUID,
    username: String,
    password: String,
    email: String
)

case class PersistentVideoRepo(ds: DataSource) extends VideoRepo {
  val ctx = new PostgresZioJdbcContext(Escape)

  import ctx._

  override def register(
      username: String,
      password: String,
      email: String
  ): Task[String] = {
    for {
      id <- Random.nextUUID
      _ <- ctx.run {
        quote {
          query[VideoTable].insertValue {
            lift(VideoTable(id, username, password, email))
          }
        }
      }
    } yield id.toString
  }.provide(ZLayer.succeed(ds))

}

object PersistentVideoRepo {
  def layer: ZLayer[Any, Throwable, PersistentVideoRepo] =
    Quill.DataSource.fromPrefix("VideoApp") >>>
      ZLayer.fromFunction(PersistentVideoRepo(_))
}
