package com.mariussoutier.play.extensions

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation._
import play.api.data.format._
import play.api.data.format.Formats._

/**
 * Form binding helpers
 */
object Formats {

  // Parse helper, taken from Play source code
  def parsing[T](parse: String => T, errMsg: String, errArgs: Seq[Any])(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
    stringFormat.bind(key, data).right.flatMap { s =>
      util.control.Exception.allCatch[T]
        .either(parse(s))
        .left.map(e => Seq(FormError(key, errMsg, errArgs)))
    }
  }

  import java.util.Locale
  implicit object LocaleFormatter extends Formatter[Locale] {
    def bind(key: String, data: Map[String, String]) = {
      val error = FormError(key, "error.required.Locale", Nil)
      val s = Seq(error)
      val k = data.get(key)
      k.toRight(s).right.flatMap {
        case str: String if (str.length == 4) => {
          val components = str.span(_ == '_')
          Right(new Locale(components._1, components._2))
        }
        case str: String if (str.length == 2) => Right(new Locale(str))
        case _ => Left(s)
      }
    }

    def unbind(key: String, locale: Locale) = Map(key -> locale.toString)
  }

  import play.api.i18n.Lang
  implicit object LangFormatter extends Formatter[Lang] {
    def bind(key: String, data: Map[String, String]) = {
      val error = FormError(key, "error.required.Lang", Nil)
      val s = Seq(error)
      val k = data.get(key)
      k.toRight(s).right.flatMap {
        case str: String if (str.length == 4) => {
          val components = str.span(_ == '_')
          Right(Lang(components._1, components._2))
        }
        case str: String if (str.length == 2) => Right(Lang(str))
        case _ => Left(s)
      }
    }

    def unbind(key: String, locale: Lang) = Map(key -> locale.toString)
  }

  implicit object BigDecimalFormatter extends Formatter[BigDecimal] {
    override val format = Some("format.numeric", Nil)

    def bind(key: String, data: Map[String, String]) = data.get(key) match {
      case Some(str: String) if (str.length > 0) => {
        try {
          Right(new BigDecimal(new java.math.BigDecimal(str)))
        } catch {
          case _ => Left(Seq(FormError(key, "error.number", Nil)))
        }
      }
      case _ => Left(Seq(FormError(key, "error.number", Nil)))
    }

    def unbind(key: String, value: BigDecimal) = Map(key -> value.toString)
  }

  sealed abstract trait Gender
  case object Male extends Gender
  case object Female extends Gender

  implicit object GenderBinder extends Formatter[Gender] {
    override val format = Some("format.gender", Nil)

    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Gender] = {
      data.get(key).flatMap { v =>
        Seq(Male, Female) find { _.toString == v }
     }.toRight(Seq(FormError(key, "error.required")))
    }

    def unbind(key: String, value: Gender): Map[String, String] = Map(key -> value.toString)
  }

  // From the Play mailing list
  def enumFormat[E <: Enumeration](enum: E): Formatter[E#Value] = new Formatter[E#Value] {
    def bind(key: String, data: Map[String, String]) = {
      stringFormat.bind(key, data).right.flatMap { s =>
        scala.util.control.Exception.allCatch[E#Value]
          .either(enum.withName(s))
          .left.map(e => Seq(FormError(key, "error.enum", Nil)))
      }
    }
    def unbind(key: String, value: E#Value) = Map(key -> value.toString)
  }

  def enum[E <: Enumeration](enum: E): Mapping[E#Value] = of(enumFormat(enum))

}
