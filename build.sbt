name := "hydra-streams"

version := "0.5.0"

organization := "io.github.carldata"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  // Scala native libraries
  "io.spray" %%  "spray-json" % "1.3.3",
  // Test dependencies
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
  "org.apache.avro" % "avro" % "1.8.2" % "test"
)

scalacOptions := Seq("-unchecked", "-deprecation")

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))

homepage := Some(url("https://github.com/carldata/hydra-streams"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/carldata/hydra-streams"),
    "scm:git@github.com:carldata/hydra-streams.git"
  )
)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

developers := List(
  Developer(
    id = "klangner",
    name = "Krzysztof Langner",
    email = "klangner@gmail.com",
    url = url("http://github/klangner")
  )
)

useGpg := true

pomIncludeRepository := { _ => false }
