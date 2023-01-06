package com.zee.auth.users

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

case class PersistentAccountRepo(ds: DataSource) extends AccountRepo {
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
  def layer: ZLayer[Any, Throwable, PersistentAccountRepo] =
    Quill.DataSource.fromPrefix("myDatabaseConfig").orDie >>>
      ZLayer.fromFunction(PersistentAccountRepo(_))
}
