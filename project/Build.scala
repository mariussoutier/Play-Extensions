import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play-extensions"
  val appVersion      = "1.0.0"

  val appDependencies = Seq.empty

  val main = play.Project(appName, appVersion, appDependencies).settings(
    organization := "com.mariussoutier"
  )

}
