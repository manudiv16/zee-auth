package com.zee.auth.entity

import io.getquill._
import io.getquill.jdbczio.Quill
import io.getquill.{Escape, PostgresZioJdbcContext}
import zio._
import zio.stream.ZStream

import java.util.UUID
import javax.sql.DataSource
import java.sql.Time
import java.time.LocalDateTime
import java.time.LocalDateTime

case class VideoTable(uuid: UUID, name: String)

case class AccountsTable(
    userId: UUID,
    username: String,
    password: String,
    email: String,
    createdOn: LocalDateTime,
    lastLogin: Option[LocalDateTime]
)

case class PersistentVideoRepo(ds: DataSource) extends AccountRepo {
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
          query[AccountsTable].insertValue {
            lift(
              AccountsTable(
                id,
                username,
                password,
                email,
                LocalDateTime.now(),
                None
              )
            )
          }
        }
      }
    } yield id.toString
  }.provide(ZLayer.succeed(ds))

  override def lookup(username: String): Task[Option[User]] = {
    if (username.isEmpty) ZIO.succeed(None)
    else
      ctx
        .run {
          quote {
            query[AccountsTable]
              .filter(p => p.username == username)
              .map(v =>
                User(
                  v.userId,
                  v.username,
                  v.email,
                  v.createdOn,
                  LocalDateTime.now()
                )
              )
          }
        }
        .provide(ZLayer.succeed(ds))
        .map(_.headOption)
  }

  override def videos: Task[List[Video]] =
    ctx
      .run {
        quote {
          query[VideoTable].map(v => Video(v.name, v.uuid))
        }
      }
      .provide(ZLayer.succeed(ds))

  override def videosStream: ZStream[Any, Throwable, Video] =
    ctx
      .stream {
        quote {
          query[VideoTable].map(v => Video(v.name, v.uuid))
        }
      }
      .provideLayer(ZLayer.succeed(ds))
}

object PersistentVideoRepo {
  def layer: ZLayer[Any, Throwable, PersistentVideoRepo] =
    Quill.DataSource.fromPrefix("AccountsApp") >>>
      ZLayer.fromFunction(PersistentVideoRepo(_))
}
