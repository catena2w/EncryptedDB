package org.encrypteddb

import org.crypto.sse.TextExtractPar
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
  val initDocs: Seq[(String, String)] = (0 until 100).map(_ => docGen.sample.get)
  val client: EDBClient = EDBClient.create()
  val server: EDBServer = EDBServer.create()
  server.update(client.insert(initDocs))
  val keywords: List[String] = TextExtractPar.lp1.keySet().asScala.toList

  property("Should be able to find documents for all extracted keywords") {
    keywords.size should be > 0
    keywords.foreach { w =>
      val result = server.search(client.search(w))
      result.size should be > 0
    }
  }

  property("Add document with a keyword") {
    forAll(docGen) { doc =>
      val content = doc._2
      // search before document addition
      val words = content.split(" ")
      val initialSearch = words.map { w =>
        w -> server.search(client.search(w)).size
      }.toMap
      // add document to database
      val insertToken = client.insert(Seq(doc))
      server.update(insertToken)

      // search after update
      words.foreach { w =>
        val docsBeforeUpdate = initialSearch(w)
        val searchToken = client.search(w)
        val docsAfterUpdate = server.search(searchToken)
        if (keywords.contains(w)) {
          docsAfterUpdate should contain(doc._1)
          docsAfterUpdate.size shouldBe docsBeforeUpdate + 1
        } else {
          docsBeforeUpdate shouldBe 0
          docsAfterUpdate.size shouldBe 0
        }
      }
    }
  }

  property("Delete keywords from search results") {
    forAll(Gen.oneOf(keywords)) { w =>
      val searchBeforeDelete = server.search(client.search(w))
      whenever(searchBeforeDelete.nonEmpty) {
        val index = Random.nextInt(searchBeforeDelete.size)
        val deletedDoc = searchBeforeDelete(index)
        val deleteToken = client.delete(w, index)
        server.update(deleteToken)
        val searchAfterDelete = server.search(client.search(w))
        searchAfterDelete.size shouldBe searchBeforeDelete.size - 1
        searchAfterDelete should not contain deletedDoc
      }
    }
  }

  property("The server should insert all tokens, even once not generated by the client") {
    // It might be non-obvious, that tokens generated without `sk` are accepted by the server, that's why this test exists
    forAll(insertTokenGen) { token =>
      server.update(token)
    }
  }

}
