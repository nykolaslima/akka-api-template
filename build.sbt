name := "akka-api-template"
version := "1.0.0"
scalaVersion := "2.11.8"

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

unmanagedSourceDirectories in Compile += baseDirectory.value / "src/main/generated-proto"

val databaseURI = new java.net.URI(
  sys.env.get("DB_URL").getOrElse("postgresql://postgres:postgres@postgres:5432/akka-api-template"))

flywayDriver := "org.postgresql.Driver"
flywayUrl := s"jdbc:${databaseURI.getScheme}://${databaseURI.getHost}:${databaseURI.getPort}${databaseURI.getPath}"
flywayUser := databaseURI.getUserInfo.split(":").head
flywayPassword := databaseURI.getUserInfo.split(":").last

val akkaV = "2.4.16"
val akkaHttpV = "10.0.1"
val json4sV = "3.4.1"
val kamonV = "0.6.3"
val slickV = "3.1.1"

lazy val commonDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "joda-time" % "joda-time" % "2.8"
)

lazy val akkaDependencies = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  "com.typesafe.akka" %% "akka-stream" % akkaV,
  "com.typesafe.akka" %% "akka-slf4j" % akkaV,
  "com.typesafe.akka" %% "akka-testkit" % akkaV,
  "com.typesafe.akka" %% "akka-http-jackson" % akkaHttpV,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV
)

lazy val akkaHttpDependencies = Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpV,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV
)

lazy val akkaComplementsDependencies = Seq(
  "de.heikoseeberger" %% "akka-http-json4s" % "1.10.1",
  "de.heikoseeberger" %% "akka-sse" % "2.0.0"
)

lazy val databaseDependencies = Seq(
  "com.typesafe.slick" %% "slick" % slickV,
  "com.typesafe.slick" %% "slick-hikaricp" % slickV,
  "org.postgresql" % "postgresql" % "9.4.1212.jre7",
  "com.github.tminglei" %% "slick-pg" % "0.14.3"
)

lazy val jsonDependencies = Seq(
  "org.json4s" %% "json4s-jackson" % json4sV,
  "org.json4s" %% "json4s-ext" % json4sV
)

lazy val googleDependencies = Seq(
  "com.google.protobuf" % "protobuf-java" % "3.1.0",
  "com.trueaccord.scalapb" %% "scalapb-runtime" % "0.5.26"
)

lazy val kamonDependencies = Seq(
  "io.kamon" %% "kamon-core" % kamonV,
  "io.kamon" %% "kamon-newrelic" % kamonV,
  "org.aspectj" % "aspectjweaver" % "1.8.10"
)

libraryDependencies ++= (
  commonDependencies ++
    akkaDependencies ++
    akkaHttpDependencies ++
    akkaComplementsDependencies ++
    databaseDependencies ++
    jsonDependencies ++
    googleDependencies ++
    kamonDependencies
  )
