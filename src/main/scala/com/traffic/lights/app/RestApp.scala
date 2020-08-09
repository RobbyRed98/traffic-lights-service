package com.traffic.lights.app

import com.traffic.lights.service.ControllerService

object RestApp extends App {
  ControllerService("localhost", 8080).start()
}
