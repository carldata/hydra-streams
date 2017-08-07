name := "hydra-streams"

version := "0.1.0"

organization := "io.github.carldata"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  // Scala native libraries
  "io.spray" %%  "spray-json" % "1.3.3",
  // Test dependencies
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
)
        