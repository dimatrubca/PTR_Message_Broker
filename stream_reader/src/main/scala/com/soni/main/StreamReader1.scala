package com.soni.main

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, Props}
import akka.io.{IO, Tcp}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, StatusCodes}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}

import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.util.matching.Regex

import org.json4s.JsonDSL._

import scala.collection.immutable
import scala.concurrent.{Await, Future}

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.model.{HttpEntity, Uri}
import akka.stream.alpakka.sse.scaladsl.EventSource
// import akka.stream.
import akka.stream.scaladsl.{Source, Sink}
import akka.stream.ThrottleMode
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.NotUsed
import akka.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.client.RequestBuilding.Get

class StreamReader1(tcpClient: ActorRef) extends Actor with akka.actor.ActorLogging {
//   val log = Logging(context.system, this)

  import akka.pattern.pipe
  import context.dispatcher

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
//   implicit val materializer: ActorMaterializer = ActorMaterializer()

  var pattern = new Regex("\\{(.*)\\}")
  var cnt = 0
  
  override def preStart(): Unit = {
        // Http(context.system).singleRequest(HttpRequest(method = HttpMethods.GET, uri = "http://localhost:4000/tweets/1")).pipeTo(self)
        println("Stream reader - preStart")
    //   log.debug("Stream reader - preStart")
    val source = "http://localhost:4000/tweets/2"
    implicit val system = ActorSystem()

    // Http()
    //   .singleRequest(Get(source))
    //   .flatMap(Unmarshal(_).to[Source[ServerSentEvent, NotUsed]])
    //   .foreach(_.runForeach(println))


// Http()
//   .singleRequest(Get("http://localhost:4000/tweets/1"))
//   .flatMap(Unmarshal(_).to[Source[ServerSentEvent, NotUsed]])
//   .foreach(_.runForeach(println))


    val send: HttpRequest => Future[HttpResponse] = Http(context.system).singleRequest(_)

    val eventSource: Source[ServerSentEvent, NotUsed] =
      EventSource(
        uri = Uri(s"http://localhost:4000/tweets/1"),
        send,
        initialLastEventId = Some("2"),
        retryDelay =Duration(1, "second")
      )
    val events: Future[immutable.Seq[ServerSentEvent]] =
      eventSource
        .throttle(elements = 2, per = Duration(500, "millis"), maximumBurst = 1, ThrottleMode.Shaping)
        .take(20)
        .runWith(Sink.seq)

      events foreach { x => {
        println(cnt)
        cnt += 1
      }}
        


        // .runForeach(println)
        // .take(nrOfSamples)
        // .runWith(Sink.seq)
    // val finished = Http(context.system).singleRequest(HttpRequest(uri = source)).flatMap { response =>
    //   response.entity.withoutSizeLimit().dataBytes.runForeach { chunk =>
    //     cnt +=1;
    //     if (cnt % 10 ==0) {
    //       println(cnt)
    //     }
    //   }
    // }
  }

  def receive = {
    case HttpResponse(StatusCodes.OK, _, entity, _) => {
        cnt = 0
        
        log.info("inside http response")
        entity.withoutSizeLimit().dataBytes.runForeach(resp => {
            val message = (pattern findFirstIn resp.utf8String).get//returns Some, need get -> { "message": ""}
            
            val pattern2 = new Regex(": panic")
            val panicMessage = pattern2 findFirstIn message

            cnt += 1;
            println(cnt);
           // println("Message: " + message)

            if (panicMessage.isEmpty) {
                val user = compact(render((parse(message) \ "message" \ "tweet" \ "user" \ "name")))
                var tweet = compact(render((parse(message) \ "message" \ "tweet" \ "text")))
                val timestamp_ms = compact(render(((parse(message) \ "message" \ "tweet" \ "timestamp_ms" ))))

                if (user != "null") {
                    val userPayload = compact(render(("timestamp_ms" -> timestamp_ms) ~ ("message" -> user)))
                    var publishUsersCommand = "pub users " + userPayload
                    // tcpClient ! ByteString(publishUsersCommand)
                }

                if (tweet != "null") {
                    val tweetPayload = compact(render(("timestamp_ms" -> timestamp_ms) ~ ("message" -> tweet)))
                    var publishTweetsCommand = "pub tweets " + tweetPayload
                    // tcpClient ! ByteString(publishTweetsCommand)
                }
            }
        })
    }
    case resp@HttpResponse(code, _, _, _) =>
        println("Request failed, response code: " + code)
        resp.discardEntityBytes()
  }


}