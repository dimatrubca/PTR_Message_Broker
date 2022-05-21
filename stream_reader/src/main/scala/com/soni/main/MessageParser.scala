package com.soni.main

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, Props}
import akka.io.{IO, Tcp}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, StatusCodes }
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.stream.scaladsl.{ Concat, Merge, Source }
import akka.stream.alpakka.sse.scaladsl.EventSource
import akka.NotUsed
import akka.util.ByteString
import scala.util.matching.Regex
import akka.http.scaladsl.model.Uri
import scala.concurrent.Future

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._


class MessageParser(tcpClient: ActorRef) extends Actor with akka.actor.ActorLogging  {
  import context.dispatcher

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  final implicit val system = context.system
  
  var pattern = new Regex("\\{(.*)\\}")

  def receive = {
    case message:String => {
        // println(message)
        log.info("received message: " + message)
    //   val message = (pattern findFirstIn eventMessage.utf8String).get //returns Some, need get -> { "message": ""}
      
      val panicPattern = new Regex(": panic")
      val panicMessage = panicPattern findFirstIn message

      if (panicMessage.isEmpty) {
          val user = compact(render((parse(message) \ "message" \ "tweet" \ "user" \ "name")))
          var tweet = compact(render((parse(message) \ "message" \ "tweet" \ "text")))
          val timestamp_ms = compact(render(((parse(message) \ "message" \ "tweet" \ "timestamp_ms" ))))

          if (user != "null") {
              val userPayload = compact(render(("timestamp_ms" -> timestamp_ms) ~ ("message" -> user)))
              var publishUsersCommand = "pub users " + userPayload

              log.info(publishUsersCommand)
              println(publishUsersCommand)

              tcpClient ! ByteString(publishUsersCommand)
          }

          if (tweet != "null") {
              val tweetPayload = compact(render(("timestamp_ms" -> timestamp_ms) ~ ("message" -> tweet)))
              var publishTweetsCommand = "pub tweets " + tweetPayload

              log.info(publishTweetsCommand)
              println(publishTweetsCommand)

              tcpClient ! ByteString(publishTweetsCommand)
          }
      }
    }
  }
}