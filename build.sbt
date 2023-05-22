ThisBuild / version := "0.1.0-SNAPSHOT"

scalaVersion := "3.2.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" %  "test"
val AkkaVersion = "2.8.0"
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion
libraryDependencies += "org.jsoup" % "jsoup" % "1.15.4"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.5.0"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.8.0"
libraryDependencies += "org.json4s" %% "json4s-native" % "4.0.6"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "4.0.6"
libraryDependencies += "io.spray" %% "spray-json" % "1.3.6"
libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc"       % "4.0.0",
  "com.h2database"  %  "h2"                % "2.1.214",
  "ch.qos.logback"  %  "logback-classic"   % "1.4.5",
  "org.scalikejdbc" %% "scalikejdbc-test" % "4.0.0" % "test",
  "org.scalikejdbc" %% "scalikejdbc-config" % "4.0.0"
)

libraryDependencies += "com.microsoft.sqlserver" % "mssql-jdbc" % "12.2.0.jre11"

libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6.1"
libraryDependencies += "org.json" % "json" % "20210307"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.8.0" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % Test
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.8.0",
  "com.typesafe.akka" %% "akka-stream" % "2.8.0",
  "com.typesafe.akka" %% "akka-protobuf-v3" % "2.8.0",
  "com.typesafe.akka" %% "akka-slf4j" % "2.8.0",
  "com.typesafe.akka" %% "akka-actor-typed" % "2.8.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.8.0" % Test
)

lazy val root = (project in file("."))
  .settings(
    name := "PTR2"
  )
