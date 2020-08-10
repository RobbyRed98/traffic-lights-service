package com.traffic.lights.service

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, concat, get, path, pathSingleSlash, _}
import akka.http.scaladsl.server.Route
import com.traffic.lights.config.{Config, ControllerConfigStore}
import com.traffic.lights.controller.{LightController, Start, Stop, Update}
import com.traffic.lights.service.util.{CORSHandler, JsonUtil}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.language.postfixOps


class ControllerService(host: String = "localhost", port: Int = 8080) {
  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  var lightController: ActorRef = system.actorOf(Props(new LightController), "light-controller")
  var binding: Future[Http.ServerBinding] = _
  var cors: CORSHandler = new CORSHandler() {}

  val route: Route = concat(
    get {
      concat(
        pathSingleSlash {
          cors.corsHandler(complete(StatusCodes.OK, HttpEntity(ContentTypes.`text/html(UTF-8)`,
            s"<html><body>The traffic lights controller service is available.</body></html>"
          )))
        },
        path("start") {
          lightController ! Start()
          cors.corsHandler(complete(StatusCodes.OK))
        },
        path("stop") {
          lightController ! Stop()
          cors.corsHandler(complete(StatusCodes.OK))
        },
        path("stop") {
          terminate()
          cors.corsHandler(complete(StatusCodes.OK))
        },
        path("config") {
          cors.corsHandler(complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, JsonUtil.toJson(ControllerConfigStore.getConfig))))
        },
        path("running") {
          cors.corsHandler(complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, ControllerConfigStore.isRunning.toString)))
        }
      )
    },
    post {
      concat(
        path("config") {
          entity(as[String]) { configString =>
            println(configString)
            val newConfig = JsonUtil.fromJson[Config](configString)
            ControllerConfigStore.updateConfig(newConfig)
            lightController ! Update(newConfig)
            cors.corsHandler(complete(StatusCodes.OK))
          }
        }
      )
    })

  def start(): Unit = binding = Http().bindAndHandle(route, host, port)
  def stop(): Unit = binding.map(_.unbind())
  def terminate(): Unit = binding.map(_.unbind()).onComplete(_ => system.terminate())
}

object ControllerService {
  def apply(host: String, port: Int): ControllerService =
    new ControllerService(host, port)
}
