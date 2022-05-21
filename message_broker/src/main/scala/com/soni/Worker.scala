package com.soni

import akka.actor.Actor
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.io.Tcp
import akka.util.ByteString


class Worker2(topicName: String) extends Actor {
  import Tcp._

  var subscribersSet = Set[ActorRef]()

  def receive = {
    ???
  }

}