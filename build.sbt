organization := "scala-dry"

name := "scala-dry"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.11.8", "2.10.6")

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:experimental.macros",
  "-unchecked",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-inaccessible",
  "-Ywarn-nullary-override",
  "-Xfuture")

resolvers += Resolver.sonatypeRepo("releases")

val paradiseVersion = "2.1.0"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "com.lihaoyi" %% "utest" % "0.4.3" % "test",
  "org.typelevel" %% "macro-compat" % "1.1.1",
  compilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full))

libraryDependencies ++= {
  if (scalaBinaryVersion.value == "2.10")
    Seq("org.scalamacros" %% "quasiquotes" % paradiseVersion cross CrossVersion.binary)
  else
    Nil
}

testFrameworks += new TestFramework("utest.runner.Framework")
