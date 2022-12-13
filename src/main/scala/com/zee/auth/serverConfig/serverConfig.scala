package com.zee.auth.serverConfig

import zio.http.{ServerConfig}

val config = ServerConfig.default.port(49192)
val configLayer = ServerConfig.live(config)
