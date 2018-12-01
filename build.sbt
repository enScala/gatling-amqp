import sbt.ExclusionRule

cancelable in Global := true

enablePlugins(GatlingPlugin)

scalaVersion := "2.12.6"

scalacOptions := Seq(
  "-encoding", "UTF-8", "-target:jvm-1.8", "-deprecation",
  "-feature", "-unchecked", "-language:implicitConversions", "-language:postfixOps")

val gatlingVersion = "2.3.1"

version := "0.11.0-SNAPSHOT"
organization := "sc.ala"
name := "gatling-amqp"
description := "Gatling AMQP support"
homepage := Some(url("https://github.com/dieselr/gatling-amqp"))
licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php"))

developers              := List(
  Developer(
    id    = "maiha",
    name  = "Kazunori Nishi",
    email = "N/A",
    url   = url("https://github.com/maiha")
  ),
  Developer(
    id    = "dieselr",
    name  = "Diesel R",
    email = "dieselr@gmail.com",
    url   = url("https://github.com/dieselr")
  )
)

(Test / test) := ((Test / test) dependsOn(Gatling / test)).value

libraryDependencies += "io.gatling.highcharts"    % "gatling-charts-highcharts"   % gatlingVersion    % Compile
libraryDependencies += "io.gatling"               % "gatling-test-framework"      % gatlingVersion    % Compile
libraryDependencies += "com.rabbitmq"             % "amqp-client"                 % "4.9.0"
libraryDependencies += "org.scalatest"            %% "scalatest"                  % "3.0.5"           % Test
libraryDependencies += "pl.project13.scala"       % "rainbow_2.11"                % "0.2" excludeAll(
  ExclusionRule("org.scala-lang.modules", "scala-xml_2.11"),
  ExclusionRule("org.scalatest", "scalatest_2.11"),
  ExclusionRule("org.scala-lang.modules", "scala-parser-combinators_2.11")
)

