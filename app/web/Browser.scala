package com.mariussoutier.play.web

import play.api.mvc.RequestHeader

case class Browser(browserType: BrowserType, version: Option[String], platform: Option[Platform] = None) {
  val name: String = browserType.toString
  lazy val isRobot = browserType == Robot
  lazy val isMobile = platform.exists { value =>
    value == iPhone || value == iPad || value == Android
  }
}

object Browser {
  def apply(implicit request: RequestHeader): Browser = request.headers.get("User-Agent").map { userAgent =>
    Browser(userAgent)
  }.getOrElse(new Browser(browserType = UnknownBrowser, version = None))

  /**
  * Examples for User Agents
  * Chrome 17:
  * Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.46 Safari/535.11
  * Safari 5:
  * Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/534.53.11 (KHTML, like Gecko) Version/5.1.3 Safari/534.53.10
  * Firefox 10:
  * Mozilla/5.0 (Macintosh; Intel Mac OS X 10.7; rv:10.0) Gecko/20100101 Firefox/10.0
  * Internet Explorer 8:
  * Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C)
  */
  def apply(userAgent: String): Browser =
    new Browser(browserType = parseBrowserName(userAgent), version = parseVersion(userAgent))

  def parseBrowserName(userAgent: String) = if (isRobot(userAgent)) Robot else userAgent match {
    case x: String if x.contains("Chrome") => Chrome
    case x: String if x.contains("MSIE") => InternetExplorer
    case x: String if x.contains("Safari") => Safari
    case x: String if x.contains("Firefox") => Firefox
    case x: String if x.contains("Opera") => Opera
    case x: String if x.contains("iCab") => iCab
    case x: String if x.contains("Netscape") => Netscape
    case x: String if x.contains("OmniWeb") => OmniWeb
    case x: String if (x.contains("Mozilla") && !x.contains("compatible")) => Mozilla
    case x: String if x.contains("Mozilla") => Netscape
    case _ => UnknownBrowser
  }

  def isRobot(userAgent: String) = false //TODO recognize robot

  def parseVersion(userAgent: String): Option[String] =
    """([0-9\.])+""".r.findFirstIn(extractVersionString(userAgent))

  private def extractVersionString(userAgent: String) = {
    val pos = userAgent match {
      case a if a.contains("Version/") => userAgent.indexOf("Version/")
      case b if b.contains("Firefox/") => userAgent.indexOf("Firefox/")
      case c if c.contains("Chrome/") => userAgent.indexOf("Chrome/")
      case d if d.contains("MSIE") => userAgent.indexOf("MSIE")
      // TODO Remaining browsers
      case _ => 0
    }
    userAgent.substring(pos)
  }

  private def parsePlatform = UnknownPlatform // TODO recognize platform
}

sealed abstract class BrowserType {
  override def toString: String = this.getClass.getSimpleName.replaceAll("""\$""", "")
}
case object Chrome extends BrowserType
case object Firefox extends BrowserType
case object Safari extends BrowserType
case object InternetExplorer extends BrowserType
case object OmniWeb extends BrowserType
case object iCab extends BrowserType
case object Opera extends BrowserType
case object Netscape extends BrowserType
case object Mozilla extends BrowserType
case object Robot extends BrowserType
case object UnknownBrowser extends BrowserType

sealed abstract class Platform
case object Mac extends Platform
case object Linux extends Platform
case object Windows extends Platform
case object iPhone extends Platform
case object iPad extends Platform
case object Android extends Platform
case object UnknownPlatform extends Platform

case class Version(major: Int, minor: Int, patch: Option[Int] = None, rest: Option[Int] = None, other: Option[String] = None) {
  override def toString =
    "" + major + "." + minor + patch.map("." + _).getOrElse("") + rest.map("." + _).getOrElse("") + other.getOrElse("")
}
