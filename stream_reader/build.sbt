name := "tcp-client"

version := "0.1"

scalaVersion := "2.12.5"
// libraryDependencies +=
//   "com.typesafe.akka" %% "akka-actor" % "2.4.20"

// // libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion
val AkkaHttpVersion = "10.2.9"
val AkkaVersion = "2.6.14"


libraryDependencies +=
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,

  "net.liftweb" %% "lift-json" % "3.4.3",
  "org.json4s" %% "json4s-jackson" % "3.7.0-M11",
    "com.lightbend.akka" %% "akka-stream-alpakka-sse" % "3.0.4",
      "org.scalatest" % "scalatest_2.10" % "2.2.1",

)
