name := "$name$"
version := (if (System.getenv("VERSION") == null) "noversion" else System.getenv("VERSION"))
scalaVersion := "2.11.8"

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

enablePlugins(JavaAppPackaging)

unmanagedSourceDirectories in Compile += baseDirectory.value / "src/main/generated-proto"

val akkaV = "2.4.16"
val akkaHttpV = "10.0.2"
val kamonV = "0.6.3"
val slickV = "3.1.1"

lazy val commonDependencies = Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "joda-time" % "joda-time" % "2.8",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.4.2" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

lazy val akkaDependencies = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  "com.typesafe.akka" %% "akka-http-jackson" % akkaHttpV,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
  "com.typesafe.akka" %% "akka-slf4j" % akkaV,
  "com.typesafe.akka" %% "akka-stream" % akkaV,
  "com.typesafe.akka" %% "akka-testkit" % akkaV
)

lazy val akkaHttpDependencies = Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpV,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % "test"
)

lazy val akkaComplementsDependencies = Seq(
  "de.heikoseeberger" %% "akka-http-json4s" % "1.11.0",
  "de.heikoseeberger" %% "akka-sse" % "2.0.0"
)

lazy val databaseDependencies = Seq(
  "com.github.tminglei" %% "slick-pg" % "0.14.3",
  "com.typesafe.slick" %% "slick" % slickV,
  "com.typesafe.slick" %% "slick-hikaricp" % slickV,
  "org.postgresql" % "postgresql" % "9.4.1212.jre7"
)

lazy val googleDependencies = Seq(
  "com.google.protobuf" % "protobuf-java" % "3.1.0",
  "com.trueaccord.scalapb" %% "scalapb-json4s" % "0.1.6",
  "com.trueaccord.scalapb" %% "scalapb-runtime" % "0.5.47"
)

lazy val kamonDependencies = Seq(
  "io.kamon" %% "kamon-core" % kamonV,
  "io.kamon" %% "kamon-newrelic" % kamonV,
  "org.aspectj" % "aspectjweaver" % "1.8.10"
)

lazy val validation = Seq(
  "org.typelevel" %% "cats" % "0.4.0",
  "com.osinka.i18n" %% "scala-i18n" % "1.0.0"
)

libraryDependencies ++= (
  commonDependencies ++
    akkaDependencies ++
    akkaHttpDependencies ++
    akkaComplementsDependencies ++
    databaseDependencies ++
    googleDependencies ++
    kamonDependencies ++
    validation
  )
