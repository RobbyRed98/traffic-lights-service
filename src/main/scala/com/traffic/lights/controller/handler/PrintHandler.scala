package com.traffic.lights.controller.handler

object PrintHandler {
  def apply(): PrintHandler = new PrintHandler()
}

class PrintHandler extends LightHandler {
  override def greenOn(): Unit = println("green on")
  override def greenOff(): Unit =  println("green off")
  override def yellowOn(): Unit =  println("yellow on")
  override def yellowOff(): Unit =  println("yellow off")
  override def redOn(): Unit = println("red on")
  override def redOff(): Unit = println("red off")
  override def yellowRedOn(): Unit = println("yellow-red on")
  override def yellowRedOff(): Unit = println("yellow-red off")
  override def allOn(): Unit = println("all on")
  override def allOff(): Unit = println("all off")
}
