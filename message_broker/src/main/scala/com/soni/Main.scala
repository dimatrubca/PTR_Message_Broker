package com.soni

import java.net.InetSocketAddress

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.ByteString


object Main {
  def main(args: Array[String]): Unit = {
    val host = "0.0.0.0"
    val port = 9900
    println(s"Server started! listening to ${host}:${port}")

    val serverProps = Props(classOf[TcpServer], new InetSocketAddress(host, port))
    val routerProps = Props(classOf[Router])

    val actorSystem: ActorSystem = ActorSystem.create("MyActorSystem")
    val serverActor: ActorRef = actorSystem.actorOf(serverProps, name="server")
    val routerActor: ActorRef = actorSystem.actorOf(routerProps, name="router")

    serverActor ! ByteString("Starting server...")
  }
}