package org.encrypteddb

import org.encrypteddb.utils.TestUtils

object DynRHBenchmark extends App with TestUtils {
  val StartDocumentsNumber: Int = 10000
  val UpdateDocumentsNumber: Int = 1000
  val KeywordSearches: Int = 1000
  val UpdateSteps: Int = 100

  // initialization
  val client: EDBClient = EDBClient.create()
  val server: EDBServer = EDBServer.create()
  val initDocs = (0 until StartDocumentsNumber).map(_ => docGen.sample.get)
  val (initTime, _) = time(server.update(client.insert(initDocs)))

  println(s"Number of documents,Update time (ms),Search time (ms)")
  // Update phase
  (0 until UpdateSteps) foreach { i =>
    val docs = (0 until UpdateDocumentsNumber).map(_ => docGen.sample.get)
    val (updateTime, _) = time(server.update(client.insert(docs)))

    val searchTime = time {
      (0 until KeywordSearches) foreach { _ =>
        val searchToken = client.search(dictionaryWordGen.sample.get)
        server.search(searchToken)
      }
    }._1 / KeywordSearches

    println(s"${StartDocumentsNumber + UpdateDocumentsNumber * i},$updateTime,$searchTime")
  }


}
