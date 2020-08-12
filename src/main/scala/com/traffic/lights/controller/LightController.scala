package com.traffic.lights.controller

import akka.actor.{Actor, ActorLogging, Cancellable}
import com.traffic.lights.config.Config
import com.traffic.lights.controller.handler.{LightHandler, PrintHandler}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

class LightController(val handler: LightHandler = PrintHandler()) extends Actor with ActorLogging {
  private val random: Random = new Random(1000)
  private var config: Option[Config] = None
  private var timeoutCancellable: Cancellable = _
  private var idThreshold: Long = 0
  private var state: State = Paused()

  override def receive: Receive = {
    case sig: Signal =>
      if (sig.id >= idThreshold)
        updateLights(sig)
      else
        log.info("Ignored color update.")
    case Start() =>
      if (state.isInstanceOf[Signal]) {
        log.info("Light controller runs already...")
        sender() ! NoChange()
      } else {
        log.info("Starting light controller...")
        sender() ! Changed()
        updateLights(Red())
      }
    case Update(newConfig: Config) =>
      log.info("Updating configuration...")
      updateConfig(newConfig)
    case Stop() =>
      if (state.isInstanceOf[Paused]) {
        log.info("Light controller is already paused...")
        sender() ! NoChange()
      } else {
        sender() ! Changed()
        timeoutCancellable.cancel()
        previousLightOff()
        state = Paused()
        handler.allOn()
        log.info("Stopping light controller.")
      }
    case GetConfig() =>
      sender() ! config.getOrElse(NoConfig())
    case GetState() =>
      if (state.isInstanceOf[Signal]) sender() ! true else sender() ! false
    case AreYouAlive() =>
      sender() ! IAmAlive()
  }

  private def updateConfig(newConfig: Config): Unit = config match {
    case Some(conf) =>
      log.info("A previous configuration has been found.")
      if (!newConfig.isSilentMergePossible(conf, state)) {
        idThreshold = System.currentTimeMillis()
        log.info("A silent update is impossible.")
        log.info("Halting current cycle...")
        timeoutCancellable.cancel()
        config = Some(newConfig)
        log.info("Applied new config.")
        log.info("Restarting cycle with red light...")
        updateLights(Red())
      } else {
        log.info("Applied new config.")
        config = Some(newConfig)
      }
    case _ =>
      log.info("No previous configuration has been found.")
      config = Some(newConfig)
      log.info("Applied new config.")
  }

  private def updateLights(signal: Signal): Unit = {
    previousLightOff()
    state = signal
    signal match {
      case Red(_) =>
        handler.redOn()
        initiateLightColorChange(YellowRed(), nextDurationOfRed())
      case YellowRed(_) =>
        handler.yellowRedOn()
        initiateLightColorChange(Green(), config.get.yellowRedLightDuration)
      case Green(_) =>
        handler.greenOn()
        initiateLightColorChange(Yellow(), config.get.greenLightDuration)
      case Yellow(_) =>
        handler.yellowOn()
        initiateLightColorChange(Red(), config.get.yellowLightDuration)
    }
  }

  private def previousLightOff(): Unit = state match {
    case Red(_) => handler.redOff()
    case YellowRed(_) => handler.yellowRedOff()
    case Green(_) => handler.greenOff()
    case Yellow(_) => handler.yellowOff()
    case Paused() => handler.allOff()
  }

  private def nextDurationOfRed(): Double = {
    config.get.lowerIntervalBorder + (random.nextDouble() % (config.get.upperIntervalBorder - config.get.lowerIntervalBorder))
  }

  private def initiateLightColorChange(nextSignal: Signal, duration: Double): Unit = {
    timeoutCancellable = context.system.scheduler.scheduleOnce(duration seconds, self, nextSignal)
  }
}

object LightController {
  def apply(): LightController = new LightController()
}
