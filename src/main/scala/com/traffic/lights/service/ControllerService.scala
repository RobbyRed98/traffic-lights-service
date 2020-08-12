package com.traffic.lights.service

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, concat, get, path, pathSingleSlash, _}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.traffic.lights.config.Config
import com.traffic.lights.controller.{AreYouAlive, Changed, GetConfig, GetState, IAmAlive, LightController, NoChange, Start, Stop, Update}
import com.traffic.lights.service.util.{CORSHandler, JsonUtil}

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import scala.language.postfixOps


class ControllerService(host: String = "localhost", port: Int = 8080) {
  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = new Timeout(5 seconds)

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
          Await.result(lightController ? Start(), 5 seconds) match {
            case NoChange() => cors.corsHandler(complete(StatusCodes.NoContent))
            case Changed() => cors.corsHandler(complete(StatusCodes.OK))
            case _ => cors.corsHandler(complete(StatusCodes.InternalServerError))
          }
        },
        path("stop") {
          Await.result(lightController ? Stop(), 5 seconds) match {
            case NoChange() => cors.corsHandler(complete(StatusCodes.NoContent))
            case Changed() => cors.corsHandler(complete(StatusCodes.OK))
            case _ => cors.corsHandler(complete(StatusCodes.InternalServerError))
          }
        },
        path("terminate") {
          terminate()
          cors.corsHandler(complete(StatusCodes.OK))
        },
        path("config") {
          Await.result(lightController ? GetConfig(), 5 seconds) match {
            case config: Config =>
              cors.corsHandler(complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, JsonUtil.toJson(config))))
            case _ => cors.corsHandler(complete(StatusCodes.NotFound))
          }
        },
        path("running") {
          Await.result(lightController ? GetState(), 5 seconds) match {
            case true => cors.corsHandler(complete(StatusCodes.OK))
            case false => cors.corsHandler(complete(StatusCodes.NoContent))
            case _ => cors.corsHandler(complete(StatusCodes.InternalServerError))
          }

        },
        path("heartbeat"){
          Await.result(lightController ? AreYouAlive(), 5 seconds) match {
            case IAmAlive() => cors.corsHandler(complete(StatusCodes.OK))
            case _ => cors.corsHandler(complete(StatusCodes.NotFound))
          }
        }
      )
    },
    post {
      concat(
        path("config") {
          entity(as[String]) { configString =>
            println(configString)
            val newConfig = JsonUtil.fromJson[Config](configString)
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
