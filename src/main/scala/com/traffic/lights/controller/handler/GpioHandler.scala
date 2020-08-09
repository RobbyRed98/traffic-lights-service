package com.traffic.lights.controller.handler

import com.pi4j.io.gpio._

class GpioHandler(greenPinId: Pin = RaspiPin.GPIO_21, yellowPinId: Pin = RaspiPin.GPIO_22,
                  redPinId: Pin = RaspiPin.GPIO_23) extends LightHandler {

  private val gpioController: GpioController = GpioFactory.getInstance()
  private val greenPin: GpioPinDigitalOutput = gpioController.provisionDigitalOutputPin(greenPinId, "green", PinState.LOW)
  private val yellowPin: GpioPinDigitalOutput = gpioController.provisionDigitalOutputPin(yellowPinId, "yellow", PinState.LOW)
  private val redPin: GpioPinDigitalOutput = gpioController.provisionDigitalOutputPin(redPinId, "red", PinState.LOW)

  greenPin.setShutdownOptions(true, PinState.LOW);
  yellowPin.setShutdownOptions(true, PinState.LOW);
  redPin.setShutdownOptions(true, PinState.LOW);

  override def greenOn(): Unit = greenPin.high()

  override def greenOff(): Unit = greenPin.low()

  override def yellowOn(): Unit = yellowPin.high()

  override def yellowOff(): Unit = yellowPin.low()

  override def redOn(): Unit = redPin.high()

  override def redOff(): Unit = redPin.low()

  override def yellowRedOn(): Unit = {
    yellowOn()
    redOn()
  }

  override def yellowRedOff(): Unit = {
    yellowOff()
    redOff()
  }

  override def allOn(): Unit = {
    greenOn()
    yellowRedOn()
  }

  override def allOff(): Unit = {
    greenOff()
    yellowRedOff()
  }
}
