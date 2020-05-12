package org.encrypteddb

import org.crypto.sse._
import org.encrypteddb.utils.TestUtils

import scala.util.Random

object DynRHBenchmark extends App with TestUtils {
  val StartDocumentsNumber: Int = 10000
  val UpdateDocumentsNumber: Int = 1000
  val KeywordSearches: Int = 1000
  val UpdateSteps: Int = 100

  // initialization
  val initDocs = (0 until StartDocumentsNumber).map(_ => docGen.sample.get)
  val (initTime, db) = time(EncryptedDB.create(initDocs))

  println(s"Number of documents,Update time (ms),Search time (ms)")
  // Update phase
  (0 until UpdateSteps) foreach { i =>
    val docs = (0 until UpdateDocumentsNumber).map(_ => docGen.sample.get)
    val (updateTime, _) = time(db.insert(docs))

    val searchTime = time {
      (0 until KeywordSearches) foreach (_ => db.search(dictionaryWordGen.sample.get))
    }._1 / KeywordSearches

    println(s"${StartDocumentsNumber + UpdateDocumentsNumber * i},$updateTime,$searchTime")
  }


}
