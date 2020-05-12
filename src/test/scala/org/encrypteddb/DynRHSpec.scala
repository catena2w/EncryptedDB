package org.encrypteddb

import org.crypto.sse.TextExtractPar
import org.encrypteddb.utils.PropertySpec

import scala.collection.JavaConverters._

class DynRHSpec extends PropertySpec {

  /**
    * Clusion framework is highly coupled and utilizes various global variables.
    * It makes hard to make really independent property tests, thus we initialize
    * one global dictionary for all tests.
    */
  val initDocs: Seq[(String, String)] = (0 until 100).map(_ => docGen.sample.get)
  val db: EncryptedDB = EncryptedDB.create(initDocs)
  val keywords: List[String] = TextExtractPar.lp1.keySet().asScala.toList

  property("Should be able to find documents for all extracted keywords") {
    keywords.size should be > 0
    keywords.foreach { w =>
      val result = db.search(w)
      result.size should be > 0
    }
  }

  property("Add document with a keyword") {
    forAll(docGen) { doc =>
      val content = doc._2
      // search before document addition
      val words = content.split(" ")
      val initialSearch = words.map(w => w -> db.search(w).size).toMap
      // add document to database
      db.insert(Seq(doc))

      // search after update
      words.foreach { w =>
        val docsBeforeUpdate = initialSearch(w)
        val docsAfterUpdate = db.search(w).size
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
