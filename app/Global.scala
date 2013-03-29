package com.mariussoutier.play.global

import java.io.File

import play.api._
import play.api.mvc._
import play.api.Logger
import play.api.mvc.Results._

/**
 * Mix this trait in your global object to have your Play load a hierarchy of configuration files
 * automatically. This is especially useful when you are working in a team where each team members
 * wants to have a different config file.
 * After Play load's the application.conf file, this trait loads the following config files,
 * overriding the values defined in application.conf.
 * In Dev mode => loads development.conf
 *                loads <username>.conf
 * In Test mode => loads test.conf
 * In Prod mode => loads prod.conf
 */
trait AutoConf extends GlobalSettings {
  import com.typesafe.config._

  override def onLoadConfig(applicationConf: Configuration, path: File, classloader: ClassLoader,
    mode: Mode.Mode) = mode match {
      case Mode.Dev => {
        val devConfig = existingFile(path, "conf/dev.conf") map { devConfigFile =>
          Logger.info("Loading test.conf")
          applicationConf ++ configForFile(devConfigFile)
        } getOrElse applicationConf

        val maybeUserConfig = for {
          userName <- applicationConf.getString("user.name")
          userNameConfigFile <- existingFile(path, s"conf/$userName.conf")
        } yield {
          Logger.info(s"Loading $userName.conf")
          devConfig ++ configForFile(userNameConfigFile)
        }

        maybeUserConfig getOrElse devConfig
      }

      case Mode.Test => existingFile(path, "conf/test.conf") map { testConfigFile =>
        Logger.info("Loading test.conf")
        applicationConf ++ configForFile(testConfigFile)
      } getOrElse applicationConf

      case Mode.Prod => existingFile(path, "conf/prod.conf") map { prodConfigFile =>
        Logger.info("Loading prod.conf")
        applicationConf ++ configForFile(prodConfigFile)
      } getOrElse applicationConf
    }

  private def existingFile(base: File, relativePath: String): Option[File] = {
    val file = new File(base, relativePath)
    if (file.exists() && !file.isDirectory())
      Some(file)
    else
      None
  }

  private def configForFile(file: File): Configuration = Configuration(ConfigFactory.parseFile(file))

}

// for Java
abstract class AutoConfSettings extends AutoConf

/**
 * Mix this trait in your GlobalSettings object to generate a 401 Moved Permanently
 * from any /url/ to /url.
 */
trait TrailingSlash extends GlobalSettings {
  override def onHandlerNotFound(request: RequestHeader): Result =
    if (request.path.endsWith("/"))
      MovedPermanently(request.path.dropRight(1))
    else
      super.onHandlerNotFound(request)
}

// for Java
abstract class TrailingSlashSettings extends TrailingSlash
