package com.traffic.lights.config

object ControllerConfigStore {
  private var config: Option[Config] = _
  private var running: Boolean = false

  def getConfig: Config =
    config.get

  def updateConfig(newConfig: Config): Unit =
    config = Some(newConfig)

  def isRunning: Boolean = running
}
