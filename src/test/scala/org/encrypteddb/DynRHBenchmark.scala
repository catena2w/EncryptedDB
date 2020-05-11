package org.encrypteddb

import org.crypto.sse._
import org.encrypteddb.utils.TestUtils

import scala.util.Random

object DynRHBenchmark extends App with TestUtils {
  val StartDocumentsNumber: Int = 10000
  val UpdateDocumentsNumber: Int = 1000
  val KeywordSearches: Int = 1000
  val UpdateSteps: Int = 100

  // generate random secret key
  val sk: Array[Byte] = RR2Lev.keyGen(256, Random.nextString(256), "salt/salt", 100000)

  // initialization
  val (initTime, emm) = time(initializeDynRH(sk, StartDocumentsNumber))

  println(s"Number of documents,Update time (ms),Search time (ms)")
  // Update phase
  (0 until UpdateSteps) foreach { i =>
    val docs = (0 until UpdateDocumentsNumber).map(_ => docGen.sample.get)
    val (updateTime, _) = time(updateDynRH(sk, emm, docs))

    val searchTime = time {
      (0 until KeywordSearches) foreach { _ =>
        searchDynRH(sk, emm, dictionaryWordGen.sample.get)
      }
    }._1 / KeywordSearches

    println(s"${StartDocumentsNumber + UpdateDocumentsNumber * i},$updateTime,$searchTime")
  }


}
