val zioVersion = "2.0.2"

lazy val root = project
  .in(file("."))
  .settings(
    inThisBuild(
      List(
        name := "zee-auth",
        organization := "com.zee",
        version := "0.0.1",
        scalaVersion := "3.2.2-RC2"
      )
    ),
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-json" % "0.3.0",
      "dev.zio" %% "zio-http" % "0.0.3",
      "com.h2database" % "h2" % "2.1.214",
      "dev.zio" %% "zio-logging" % "2.1.3",
      "io.getquill" %% "quill-zio" % "4.6.0",
      "nl.vroste" %% "zio-amqp" % "0.4.0",
      "org.postgresql" % "postgresql" % "42.2.8",
      "io.getquill" %% "quill-jdbc-zio" % "4.6.0",
      "dev.zio" %% "zio-test" % zioVersion % Test,
      "com.github.jwt-scala" %% "jwt-core" % "9.1.1",
      "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
      "dev.zio" %% "zio-test-junit" % zioVersion % Test,
      "dev.zio" %% "zio-test-magnolia" % zioVersion % Test
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )
