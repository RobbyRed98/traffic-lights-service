package com.traffic.lights.config

import com.traffic.lights.controller._

class Config(var upperIntervalBorder: Double, var lowerIntervalBorder: Double, var greenLightDuration: Double,
             var yellowLightDuration: Double, var yellowRedLightDuration: Double) {

  def isSilentMergePossible(other: Config, currentState: State): Boolean = currentState match {
    case Red(_) =>
      upperIntervalBorder == other.upperIntervalBorder && lowerIntervalBorder == other.lowerIntervalBorder
    case Yellow(_) =>
      yellowLightDuration == other.yellowLightDuration
    case YellowRed(_) =>
      yellowRedLightDuration == other.yellowRedLightDuration
    case Green(_) =>
      greenLightDuration == other.greenLightDuration
    case Paused() => true
  }
}

object Config {
  def apply(upperIntervalBorder: Double, lowerIntervalBorder: Double, greenLightDuration: Double,
            yellowLightDuration: Double, yellowRedLightDuration: Double): Config =
    new Config(upperIntervalBorder, lowerIntervalBorder, greenLightDuration, yellowLightDuration, yellowRedLightDuration)
}