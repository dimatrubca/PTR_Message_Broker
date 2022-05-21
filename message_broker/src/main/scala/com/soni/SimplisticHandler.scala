package com.soni

import akka.actor.Actor
import akka.actor.{ActorRef, ActorSystem, ActorSelection, Props}
import akka.io.Tcp
import akka.util.ByteString

import org.json4s._
import org.json4s.jackson.JsonMethods._




class SimplisticHandler extends Actor {
  import Tcp._

  implicit val formats: Formats = DefaultFormats // Brings in default date formats etc.

  val router: ActorSelection = context.system.actorSelection("user/router")


  def receive = {
    case Received(data) =>
      println(s"Data received - ${data.utf8String}")

      var trimmedData = data.utf8String.replaceAll("""(?m)\s+$""", "")

      val dataSplitted = trimmedData.split(" +")

      val firstDelimitterPos = trimmedData.indexOf(" ", 0);
      val secondDelimitterPos = trimmedData.indexOf(" ", trimmedData.indexOf(" ") + 1);
      println(firstDelimitterPos)
      println(secondDelimitterPos)

      var command: String = trimmedData.substring(0, firstDelimitterPos)
      var topic: String = null
      var payload: String = null

      if (secondDelimitterPos != -1) {
        topic = trimmedData.substring(firstDelimitterPos + 1, secondDelimitterPos)
        payload = trimmedData.substring(secondDelimitterPos + 1)
      } else {
        topic = trimmedData.substring(firstDelimitterPos + 1)
      }

      println("command: " + command)
      println("topic: " + topic)
      println("payload: " + payload)

      router ! ParsedCommand(command, topic, payload, sender())
      sender() ! Write(ByteString("SERVER_RES: ").concat(data))

    case PeerClosed     => context stop self
  }
}