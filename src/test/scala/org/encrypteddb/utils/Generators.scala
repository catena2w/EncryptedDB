package org.encrypteddb.utils

import java.util.Comparator

import com.google.common.collect.{Ordering, TreeMultimap}
import org.encrypteddb.InsertToken
import org.scalacheck.{Arbitrary, Gen}

trait Generators {

  private val DictionarySize = 100

  private lazy val randomWordGenerator: Gen[String] = Gen.nonEmptyListOf(Gen.alphaChar).map(_.mkString.take(10))

  // fix our dictionary to have 1000 words
  lazy val dictionary: Seq[String] = Gen.listOfN(DictionarySize, randomWordGenerator).sample.get

  lazy val dictionaryWordGen: Gen[String] = Gen.oneOf(dictionary)

  // generate a sentence with words from dictionary
  lazy val docGen: Gen[(String, String)] = for {
    id <- randomWordGenerator
    content <- Gen.nonEmptyListOf(dictionaryWordGen).map(_.mkString(" "))
  } yield (id, content)

  def genBoundedBytes(minSize: Int, maxSize: Int): Gen[Array[Byte]] = {
    Gen.choose(minSize, maxSize) flatMap { sz => Gen.listOfN(sz, Arbitrary.arbitrary[Byte]).map(_.toArray) }
  }

  lazy val insertTokenGen: Gen[InsertToken] = for {
    key <- Arbitrary.arbitrary[String]
    value <- genBoundedBytes(1, 64)
  } yield {
    val comparator: Comparator[String] = Ordering.natural()
    val map: TreeMultimap[String, Array[Byte]] = TreeMultimap.create(comparator, Ordering.usingToString)
    map.put(key, value)
    InsertToken(map)
  }

}
