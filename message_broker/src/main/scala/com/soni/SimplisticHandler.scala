package com.soni

import akka.actor.Actor
import akka.actor.{ActorRef, ActorSystem, ActorSelection, Props}
import akka.io.Tcp
import akka.util.ByteString

import org.json4s._
import org.json4s.jackson.JsonMethods._




class SimplisticHandler extends Actor {
  import Tcp._

  var topicSupervisorMap = Map[String, ActorRef]()
  implicit val formats: Formats = DefaultFormats // Brings in default date formats etc.

  val router: ActorSelection = context.system.actorSelection("user/router")


  def receive = {
    case Received(data) =>
      println(s"Data received - ${data.utf8String}")

      val dataSplitted = data.utf8String.split(" +")
      var command: String = dataSplitted(0)
      var topic: String = dataSplitted(1)
      var payload: String = null

      if (dataSplitted.length > 2) {
        payload = dataSplitted(2)
      }

      router ! ParsedCommand(command, topic, payload, sender())
      sender() ! Write(ByteString("SERVER_RES: ").concat(data))

      // val receivedMessage = parse(data.utf8String).extract[ReceivedRequest]

      // println("parsed received message:")
      // println(receivedMessage)

      // val requestType = receivedMessage.requestType
      // println("request type:", requestType)
      // // val topic = receivedMessage.topic
      // val topic = "Pacific"

      // if (!(topicSupervisorMap.isDefinedAt(topic))) {
      //   println("Creating supervisor for topic: " + topic + " self: " + self)
      //   println("map: " + topicSupervisorMap)
      //   val newTopicSupervisor = context.actorOf(Props(classOf[TopicSupervisor], topic))
      //   topicSupervisorMap += (topic -> newTopicSupervisor)
      // }

      // val topicSupervisor = topicSupervisorMap(topic)

      // if (requestType == "subscribe") {
      //   println("Notifying superfisor, subscribe")
      //   topicSupervisor ! Subscribe(sender()) 
      // } else if (requestType == "unsubscribe") {
      //   topicSupervisor ! Unsubscribe(sender())
      // } else if (requestType == "publish") {
      //   println("publish event")
      //   val message = new PublisherMessage(receivedMessage.payload.get)

      //   topicSupervisor ! Publish(sender(), message) 
      // }

    case PeerClosed     => context stop self
  }
}