package com.soni

import akka.actor.Actor
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.io.Tcp
import akka.util.ByteString


class TopicSupervisor(topicName: String) extends Actor with akka.actor.ActorLogging  {
  import Tcp._

  var subscribersSet = Set[ActorRef]()

  def receive = {
    case Publish(sender: ActorRef, publisherMessage: PublisherMessage) => {
      log.info("Inside topic supervisor: ")
      log.info("Inside topic supervisor: " + topicName)
      log.info("Subscribers:" + subscribersSet)
      
      subscribersSet.foreach(subscriber => {
        log.info("writing to address: " + subscriber)
        subscriber ! Write(ByteString(publisherMessage.payload))
      })

    }
    case Subscribe(sender: ActorRef) => {
      subscribersSet += sender // if exists ???
      log.info("Supervisor subscribe")
      log.info("sender: " + sender)
      log.info("subscribers: " + subscribersSet)
    }

    case Unsubscribe(sender: ActorRef) => {
      subscribersSet -= sender // what if wasn't???
    }
  }

}