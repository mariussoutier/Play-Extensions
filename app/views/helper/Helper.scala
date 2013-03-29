package views.html.helper

import play.api.templates.Html
import play.api.mvc.RequestHeader

/*
  Not everything can be solved with a template. Sometimes you need a Scala object, e.g. when
  you want to use generics.
*/


/**
  * Same as Play's repeat helper but also provides you with an index.
  */
object repeatWithIndex {
  def apply(field: play.api.data.Field, min: Int = 1)(f: (play.api.data.Field, Int) => Html) = {
    (0 until math.max(if (field.indexes.isEmpty) 0 else field.indexes.max + 1, min)).map(i => f(field("[" + i + "]"), i))
  }
}

/* Display multiple optional Strings as a comma separated String */
object commaSeparated {
  def apply(components: Option[String]*) = components.flatten.mkString(", ")
}

object ifGreaterThanZero {
  def apply(t: Int)(block: (Int) => Html) =
    if (t > 0) block(t) else Html("")
}

object definingNonEmpty {
  def apply[T <: Seq[_]](t: T)(block: (T) => Html) = {
    if (t.nonEmpty) block(t) else Html("")
  }
}

object nonEmptyOrElse {
  def apply[T <: Seq[_]](t: T)(nonEmptyBlock: (T) => Html)(emptyBlock: => Html) = {
    if (t.nonEmpty) nonEmptyBlock(t) else emptyBlock
  }
}

object mapOrElse {
  def apply[T](t: Seq[T])(nonEmptyBlock: (T) => Html)(emptyBlock: => Html) = {
    if (t.nonEmpty) t.map(nonEmptyBlock(_)) else emptyBlock
  }
}

object ifAnon {
  def apply(sessionKey: String)(block: => Html)(implicit request: RequestHeader) = {
    if (request.session.get(sessionKey).isEmpty) block else Html.empty
  }
}

object `package` {
  /* Abbreviate text after a given number of sentences */
  def abbreviatePhrases(s: String, phrases: Int = 1): String = {
    import scala.math.min
    @scala.annotation.tailrec
    def nextPhrase(text: String, pos: Int, phraseCount: Int): String = {
      if (phraseCount >= phrases || pos >= s.length) text
      else {
        val endOfPhrase = findNearest(pos)
        if (endOfPhrase == -1) text else {
          val phrase = s.substring(pos, endOfPhrase + 1)
          nextPhrase(text + phrase, endOfPhrase, phraseCount + 1)
        }
      }
    }
    def findNearest(pos: Int, lookFor: Seq[String] = Vector(".", "?", "!")): Int = {
      val positions = lookFor.map(s.indexOf(_, pos)).filter(_ != -1)
      if (positions.nonEmpty) positions.min else -1 //min crashes on empty seq
    }

    if (s.isEmpty) "" else nextPhrase("", 0, 0)
  }
}
