logLevel := Level.Warn

resolvers += "Flyway" at "https://flywaydb.org/repo"

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.5")
addSbtPlugin("org.flywaydb" % "flyway-sbt" % "4.0.3")
