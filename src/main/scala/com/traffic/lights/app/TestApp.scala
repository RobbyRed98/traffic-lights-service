package com.traffic.lights.app

import akka.actor.{ActorSystem, Props}
import com.traffic.lights.config.Config
import com.traffic.lights.{LightController, Start, Stop, Update}

object TestApp extends App {
  val config: Config = new Config(8, 10, 5, 5,5)
  val lightController = ActorSystem("traffic-lights").actorOf(Props(new LightController), "lightController")
  lightController ! Update(config)
  lightController ! Start()
  Thread.sleep(13000)
  lightController ! Stop()
}
