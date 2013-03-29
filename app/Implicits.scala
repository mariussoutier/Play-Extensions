package com.mariussoutier.play

/**
  * Collection of useful implicits.
  */
object Implicits {

  implicit def liftOption[T](optionalValue: T): Option[T] = Some(optionalValue)

  implicit class StringExtensions(string: String) {
    // Stolen from Play1
    def slugify(lowercase: Boolean = true) = {
      var slugifiedString = string
      slugifiedString = noAccents(slugifiedString)
      slugifiedString = slugifiedString.replaceAll("([a-z])'s([^a-z])", "$1s$2")
      slugifiedString = slugifiedString.replaceAll("[^\\w]", "-").replaceAll("-{2,}", "-")
      // Get rid of any - at the start and end.
      slugifiedString = slugifiedString.replaceAll("-+$", "").replaceAll("^-+", "")

      if (lowercase) slugifiedString.toLowerCase else slugifiedString
    }

    private def noAccents(string: String) = {
      import java.text.Normalizer
      // Longest chain ever
      Normalizer.normalize(string, Normalizer.Form.NFKC).
        replaceAll("[äÄ]", "ae").replaceAll("öÖ", "oe").replaceAll("üÜ", "ue").
        replaceAll("[àáâãåāąă]", "a").replaceAll("[çćčĉċ]", "c").replaceAll("[ďđð]", "d").replaceAll("[èéêëēęěĕė]", "e").replaceAll("[ƒſ]", "f").replaceAll("[ĝğġģ]", "g").replaceAll("[ĥħ]", "h").replaceAll("[ìíîïīĩĭįı]", "i").replaceAll("[ĳĵ]", "j").replaceAll("[ķĸ]", "k").replaceAll("[łľĺļŀ]", "l").replaceAll("[ñńňņŉŋ]", "n").replaceAll("[òóôõöøōőŏœ]", "o").replaceAll("[Þþ]", "p").replaceAll("[ŕřŗ]", "r").replaceAll("[śšşŝș]", "s").replaceAll("[ťţŧț]", "t").replaceAll("[ùúûüūůűŭũų]", "u").replaceAll("[ŵ]", "w").replaceAll("[ýÿŷ]", "y").replaceAll("[žżź]", "z").replaceAll("[æ]", "ae").replaceAll("[ÀÁÂÃÄÅĀĄĂ]", "A").replaceAll("[ÇĆČĈĊ]", "C").replaceAll("[ĎĐÐ]", "D").replaceAll("[ÈÉÊËĒĘĚĔĖ]", "E").replaceAll("[ĜĞĠĢ]", "G").replaceAll("[ĤĦ]", "H").replaceAll("[ÌÍÎÏĪĨĬĮİ]", "I").replaceAll("[Ĵ]", "J").replaceAll("[Ķ]", "K").replaceAll("[ŁĽĹĻĿ]", "L").replaceAll("[ÑŃŇŅŊ]", "N").replaceAll("[ÒÓÔÕÖØŌŐŎ]", "O").replaceAll("[ŔŘŖ]", "R").replaceAll("[ŚŠŞŜȘ]", "S").replaceAll("[ÙÚÛÜŪŮŰŬŨŲ]", "U").replaceAll("[Ŵ]", "W").replaceAll("[ÝŶŸ]", "Y").replaceAll("[ŹŽŻ]", "Z").replaceAll("[ß]", "ss")
    }

    // Returns a seq containing, in order, each path component of the receiver.
    def pathComponents: Seq[String] = if (string.isEmpty) Seq.empty else {
      val components: Seq[String] = string.split("/").filterNot(_.isEmpty).toSeq
      if (components.isEmpty) Seq("/") else {
        val componentsStart: Seq[String] = if (string.startsWith("/")) Seq("/") ++ components else components
        val componentsStartEnd: Seq[String] = if (string.endsWith("/")) componentsStart :+ "/" else componentsStart
        componentsStartEnd
      }
    }
  }


}


