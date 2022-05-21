package com.soni

import akka.util.ByteString
import akka.actor.ActorRef


case class ParsedCommand(commandType: String, topic: String, payload: String, sender: ActorRef)

case class ReceivedRequest(requestType: String, topic: String, payload: String)
case class PublisherMessage(payload: String)

case class Subscribe(address: ActorRef)
case class Unsubscribe(address: ActorRef)
case class Publish(address: ActorRef, message: PublisherMessage)