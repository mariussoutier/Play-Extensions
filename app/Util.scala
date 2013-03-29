package com.mariussoutier.play.util

object Util {

  def UUID: String = java.util.UUID.randomUUID().toString

  def randomDigits(numberOfDigits: Int): String =
    (for (i <- Range(0, numberOfDigits)) yield new scala.util.Random().nextInt(10)).mkString

  def randomLetters(numberOfLetters: Int): String =
    (for (i <- Range(0, numberOfLetters)) yield randomChar(_.isLetter)).mkString

  private def randomChar(predicate: Char => Boolean): Char = {
    val rand = new scala.util.Random
    var r: Char = rand.nextPrintableChar
    while (!predicate(r))
      r = rand.nextPrintableChar
    r
  }

  /** Executes a given block of code and prints the elapsed time */
  def time[T](block: => T): T = {
    val start = System.currentTimeMillis
    val res = block
    val totalTime = System.currentTimeMillis - start
    println("Elapsed time: %1d ms".format(totalTime))
    res
  }

}
