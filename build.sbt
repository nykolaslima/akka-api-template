val ver = if (System.getenv("VERSION") == null) "noversion" else System.getenv("VERSION")

name := "akka-api"
version := ver
scalaVersion := "2.11.8"

resolvers += Resolver.bintrayRepo("hseeberger", "maven")
enablePlugins(JavaAppPackaging)
enablePlugins(UniversalPlugin)
unmanagedSourceDirectories in Compile += baseDirectory.value / "src/main/generated-proto"

val akkaV = "2.4.16"
val akkaHttpV = "10.0.1"
val json4sV = "3.4.1"
val kamonV = "0.6.3"
val slickV = "3.1.1"

libraryDependencies ++= (Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "com.github.tminglei" %% "slick-pg" % "0.14.3",
  "com.google.protobuf" % "protobuf-java" % "3.1.0",
  "com.trueaccord.scalapb" %% "scalapb-runtime" % "0.5.26",
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  "com.typesafe.akka" %% "akka-http" % akkaHttpV,
  "com.typesafe.akka" %% "akka-http-jackson" % akkaHttpV,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV,
  "com.typesafe.akka" %% "akka-slf4j" % akkaV,
  "com.typesafe.akka" %% "akka-stream" % akkaV,
  "com.typesafe.akka" %% "akka-testkit" % akkaV,
  "com.typesafe.slick" %% "slick" % slickV,
  "com.typesafe.slick" %% "slick-hikaricp" % slickV,
  "de.heikoseeberger" %% "akka-http-json4s" % "1.10.1",
  "de.heikoseeberger" %% "akka-sse" % "2.0.0",
  "joda-time" % "joda-time" % "2.8",
  "org.json4s" %% "json4s-ext" % json4sV,
  "org.json4s" %% "json4s-jackson" % json4sV,
  "org.postgresql" % "postgresql" % "9.4.1212.jre7",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.4.2" % Test,
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
))
