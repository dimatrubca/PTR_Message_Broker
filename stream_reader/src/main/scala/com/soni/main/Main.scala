package com.soni.main

import java.net.InetSocketAddress

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.ByteString
import com.soni.client.TcpClient

object Main {
  def main(args: Array[String]): Unit = {
    val host = "localhost"
    val port = 9900
    println(s"Started client! connecting to ${host}:${port}")

    val clientProps = Props(classOf[TcpClient], new InetSocketAddress(host, port), null)

    val actorSystem: ActorSystem = ActorSystem.create("MyActorSystem")
    // val clientActor: ActorRef = actorSystem.actorOf(clientProps)
    val streamReader: ActorRef = actorSystem.actorOf(Props(classOf[StreamReader1], null))

    Thread.sleep(2000)
    // clientActor ! ByteString("hello from client!\nhello from client!\nhello from client!\n")
    // clientActor ! ByteString("second client message")
  }
}