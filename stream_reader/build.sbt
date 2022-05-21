name := "tcp-client"

version := "0.1"

scalaVersion := "2.12.5"


// libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion
val AkkaHttpVersion = "10.2.9"
val AkkaVersion = "2.6.14"

libraryDependencies +=
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.lightbend.akka" %% "akka-stream-alpakka-sse" % "3.0.4",
  "com.typesafe.play" %% "play-json" % "2.9.2",
  "org.mongodb.scala" %% "mongo-scala-driver" % "4.5.0",
  "org.json4s" %% "json4s-jackson" % "3.7.0-M11",

)
