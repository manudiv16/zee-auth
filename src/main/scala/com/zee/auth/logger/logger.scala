package com.zee.auth.logger

import zio.{Runtime, ZIOAppDefault, ZIO, Console}
import zio.logging.{LogFormat, console}

val logger =
  Runtime.removeDefaultLoggers >>> console(LogFormat.colored)
