// package com.soni.client

// import java.net.InetSocketAddress

// import akka.actor.{Actor, ActorRef, Props}
// import akka.io.{IO, Tcp}
// import akka.util.ByteString
// import java.net.http.HttpRequest
// import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
// import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, StatusCodes}
// import akka.http.scaladsl.Http


// class Worker extends Actor {

//   import akka.pattern.pipe
//   import context.dispatcher

//   final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

//   override def preStart(): Unit = {
//   }

//   def receive = {
//       case string => true
//     // case Work(message, uuid) => {
//     //     // check if contains panic then pass
//     //     1

//     // }
//   }


// }