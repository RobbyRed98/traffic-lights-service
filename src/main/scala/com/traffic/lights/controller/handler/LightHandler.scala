package com.traffic.lights.controller.handler

trait LightHandler {
  def greenOn(): Unit
  def greenOff(): Unit
  def yellowOn(): Unit
  def yellowOff(): Unit
  def redOn(): Unit
  def redOff(): Unit
  def yellowRedOn(): Unit
  def yellowRedOff(): Unit
  def allOn(): Unit
  def allOff(): Unit
}
