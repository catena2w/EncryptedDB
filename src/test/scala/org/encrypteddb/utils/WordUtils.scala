package org.encrypteddb.utils

import org.scalacheck.Gen

trait WordUtils {

  private val DictionarySize = 100

  private lazy val randomWordGenerator: Gen[String] = Gen.nonEmptyListOf(Gen.alphaChar).map(_.mkString.take(10))

  // fix our dictionary to have 1000 words
  lazy val dictionary: Seq[String] = Gen.listOfN(DictionarySize, randomWordGenerator).sample.get

  lazy val keywordGen: Gen[String] = Gen.oneOf(dictionary)

  // generate a sentence with words from dictionary
  lazy val sentenceGen: Gen[String] = Gen.nonEmptyListOf(keywordGen).map(_.mkString(" "))

}
