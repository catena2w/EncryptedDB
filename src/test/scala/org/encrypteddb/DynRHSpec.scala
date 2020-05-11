package org.encrypteddb

import java.io.File
import java.util

import com.google.common.collect.ArrayListMultimap
import org.crypto.sse.{DynRH, RR2Lev, TextExtractPar, TextProc}
import org.encrypteddb.utils.PropertySpec
import org.scalacheck.Gen

import scala.collection.JavaConverters._
import scala.util.Random

class DynRHSpec extends PropertySpec {


  /**
    * Clusion framework is highly coupled and utilizes various global variables.
    * It makes hard to make really independent property tests, thus we initialize
    * one global dictionary for all tests.
    */
  val sk: Array[Byte] = RR2Lev.keyGen(256, Random.nextString(256), "salt/salt", 100000)
  val emm: util.HashMap[String, Array[Byte]] = initializeDynRH(sk, 100)
  val keywords: List[String] = TextExtractPar.lp1.keySet().asScala.toList

  property("Should be able to find documents for all extracted keywords") {
    keywords.foreach { w =>
      val result = searchDynRH(sk, emm, w)
      result.size should be > 0
    }
  }

  property("Add document with a keyword") {
    forAll(docGen) { doc =>
      // search before document addition
      val words = doc.split(" ")
      val initialSearch = words.map(w => w -> searchDynRH(sk, emm, w).size).toMap

      // add document to database
      updateDynRH(sk, emm, Seq(doc))

      // search after update
      words.foreach { w =>
        val docsBeforeUpdate = initialSearch(w)
        val docsAfterUpdate = searchDynRH(sk, emm, w).size
        if (keywords.contains(w)) {
          docsAfterUpdate shouldBe docsBeforeUpdate + 1
        } else {
          docsBeforeUpdate shouldBe 0
          docsAfterUpdate shouldBe 0
        }
      }
    }
  }

}
