package com.traffic.lights.controller

import com.traffic.lights.config.Config

trait State
case class Paused() extends State

trait Signal extends State {val id: Long}
case class Green(id: Long = System.currentTimeMillis()) extends Signal
case class Yellow(id: Long = System.currentTimeMillis()) extends Signal
case class YellowRed(id: Long = System.currentTimeMillis()) extends Signal
case class Red(id: Long = System.currentTimeMillis()) extends Signal

trait Instruction
case class Start() extends Instruction
case class Update(config: Config) extends Instruction
case class Stop() extends Instruction
case class GetConfig() extends Instruction
case class GetState() extends Instruction

trait Reply
case class NoChange() extends Reply
case class Changed() extends Reply
case class NoConfig() extends Reply

trait HeartBeat
case class AreYouAlive() extends HeartBeat
case class IAmAlive() extends HeartBeat