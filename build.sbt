import sbt.ExclusionRule
import ReleaseTransformations._

cancelable in Global := true

enablePlugins(GatlingPlugin)

organization  := "sc.ala"
name          := "gatling-amqp"
description   := "Gatling AMQP library extension"
homepage      := Some(url("https://github.com/dieselr/gatling-amqp"))
licenses      := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php"))
scalacOptions := Seq(
  "-encoding", "UTF-8", "-target:jvm-1.8", "-deprecation",
  "-feature", "-unchecked", "-language:implicitConversions", "-language:postfixOps"
)

developers              := List(
  Developer(
    id    = "maiha",
    name  = "Kazunori Nishi",
    email = "N/A",
    url   = url("https://github.com/maiha")
  ),
  Developer(
    id    = "dieselr",
    name  = "DieselR",
    email = "dieselr@gmail.com",
    url   = url("https://github.com/dieselr")
  )
)

useGpg := true
pgpSecretRing := pgpPublicRing.value

scmInfo := Some(
  ScmInfo(
    url("https://github.com/dieselr/gatling-amqp"),
    "scm:git@github.com:dieselr/gatling-amqp.git"
  )
)

pomIncludeRepository := { _ => false }
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
publishMavenStyle := true

releasePublishArtifactsAction := PgpKeys.publishSigned.value
releaseTagName := s"${if (releaseUseGlobalVersion.value) (version in ThisBuild).value else version.value}"
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
//  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  releaseStepCommand("sonatypeOpen \"sc.ala\" \"gatling-amqp\""),
  releaseStepCommand("publishLocalSigned"),
  releaseStepCommand("sonatypeRelease"),
)

(Test / test) := ((Test / test) dependsOn(Gatling / test)).value

val gatlingVersion = "2.3.1"

libraryDependencies += "io.gatling.highcharts"    % "gatling-charts-highcharts"   % gatlingVersion    % Compile
libraryDependencies += "io.gatling"               % "gatling-test-framework"      % gatlingVersion    % Compile
libraryDependencies += "com.rabbitmq"             % "amqp-client"                 % "4.9.0"
libraryDependencies += "org.scalatest"            %% "scalatest"                  % "3.0.5"           % Test
libraryDependencies += "pl.project13.scala"       % "rainbow_2.11"                % "0.2" excludeAll(
  ExclusionRule("org.scala-lang.modules", "scala-xml_2.11"),
  ExclusionRule("org.scalatest", "scalatest_2.11"),
  ExclusionRule("org.scala-lang.modules", "scala-parser-combinators_2.11")
)

