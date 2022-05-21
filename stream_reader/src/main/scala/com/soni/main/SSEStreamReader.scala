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
import scala.util.matching.Regex
import akka.http.scaladsl.model.Uri
import scala.concurrent.Future

class SSEStreamReader(messageParser: ActorRef) extends Actor with akka.actor.ActorLogging  {
  import context.dispatcher

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  final implicit val system = context.system
  
  var pattern = new Regex("\\{(.*)\\}")

  override def preStart(): Unit = {
    log.debug("Stream reader - preStart")
    
    val send: HttpRequest => Future[HttpResponse] = Http().singleRequest(_)

    val tweetsSource1: Source[ServerSentEvent, NotUsed] =
      EventSource(
        uri = Uri("http://localhost:4000/tweets/1"),
        send
      )
    val tweetsSource2: Source[ServerSentEvent, NotUsed] = 
      EventSource(
        uri = Uri("http://localhost:4000/tweets/2"),
        send
      )

    Source.combine(tweetsSource1, tweetsSource2)(Merge(_)).runForeach(e => self ! e)
    println("inside stream reader prestart - end")
  }

  def receive = {
    case event:ServerSentEvent => {
      // log.info(event.data.getClass)
      // println(event.data.getClass.toString)
      messageParser ! event.data
      // println("\n\n\nReceived!\n")
      // val message = (pattern findFirstIn resp.utf8String).get //returns Some, need get -> { "message": ""}
      
      // val panicPattern = new Regex(": panic")
      // val panicMessage = pattern2 findFirstIn message

      // cnt += 1
      // println(cnt)

      // if (panicMessage.isEmpty) {
      //     val user = compact(render((parse(message) \ "message" \ "tweet" \ "user" \ "name")))
      //     var tweet = compact(render((parse(message) \ "message" \ "tweet" \ "text")))
      //     val timestamp_ms = compact(render(((parse(message) \ "message" \ "tweet" \ "timestamp_ms" ))))

      //     if (user != "null") {
      //         val userPayload = compact(render(("timestamp_ms" -> timestamp_ms) ~ ("message" -> user)))
      //         var publishUsersCommand = "pub users " + userPayload
      //         tcpClient ! ByteString(publishUsersCommand)
      //     }

      //     if (tweet != "null") {
      //         val tweetPayload = compact(render(("timestamp_ms" -> timestamp_ms) ~ ("message" -> tweet)))
      //         var publishTweetsCommand = "pub tweets " + tweetPayload
      //         tcpClient ! ByteString(publishTweetsCommand)
      //     }
      // }
    }
  }
}