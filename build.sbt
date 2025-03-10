

name := "traffic-lights-service"

version := "0.1"

maintainer := "Robin Roth <robin.roth@web.de>"
packageSummary := "A REST-service to control a traffic light."
packageDescription := """A REST-service to control a real traffic light. The traffic light is connected to the GPIOs of a Raspberry Pi (Zero WH)."""
debianPackageDependencies := Seq("openjdk-11-jre-headless")

scalaVersion := "2.13.3"

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-actor
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.8"
// https://mvnrepository.com/artifact/com.typesafe.akka/akka-http
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.12"
// https://mvnrepository.com/artifact/com.typesafe.akka/akka-http-core
libraryDependencies += "com.typesafe.akka" %% "akka-http-core" % "10.1.12"
// https://mvnrepository.com/artifact/com.typesafe.akka/akka-stream
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.8"
// https://mvnrepository.com/artifact/com.pi4j/pi4j-core
libraryDependencies += "com.pi4j" % "pi4j-core" % "1.2"
// https://mvnrepository.com/artifact/com.google.code.gson/gson
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.6"

enablePlugins(JavaServerAppPackaging)
enablePlugins(SystemdPlugin)

