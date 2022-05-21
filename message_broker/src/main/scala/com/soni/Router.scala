package com.soni

import akka.actor.Actor
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.io.Tcp
import akka.util.ByteString

import org.json4s._
import org.json4s.jackson.JsonMethods._


class Router extends Actor with akka.actor.ActorLogging  {
    var topicSupervisorMap = Map[String, ActorRef]()


    def receive = {
        case ParsedCommand(requestType, topic, payload, messageSender) => {
            log.info("inside router, topic: " + topic + "!")

            if (!(topicSupervisorMap.isDefinedAt(topic))) {
                println("new topic: " + topic)
                val newTopicSupervisor = context.actorOf(Props(classOf[TopicSupervisor], topic))
                topicSupervisorMap += (topic -> newTopicSupervisor)
            }

            val topicSupervisor = topicSupervisorMap(topic)

            if (requestType == "sub") {
                topicSupervisor ! Subscribe(messageSender)
            } else if (requestType == "unsub") {
                topicSupervisor !  Unsubscribe(messageSender)
            } else if (requestType == "pub") {
                topicSupervisor ! Publish(messageSender, PublisherMessage(payload))
            }
        }
    }
}