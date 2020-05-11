package org.encrypteddb

import java.io.{BufferedReader, File, InputStreamReader}
import java.util
import java.util.HashMap

import com.google.common.collect.ArrayListMultimap
import org.crypto.sse._
import org.encrypteddb.utils.TestUtils

import scala.util.Random

object DynRHBenchmark extends App with TestUtils {
  val StartDocumentsNumber: Int = 10000
  val UpdateDocumentsNumber: Int = 1000
  val KeywordSearches: Int = 1000
  val UpdateSteps: Int = 100

  // generate secret key
  val sk = RR2Lev.keyGen(256, Random.nextString(256), "salt/salt", 100000)

  // initialization
  val (initTime, emm) = time(initialize(StartDocumentsNumber))

  println(s"Number of documents,Update time (ms),Search time (ms)")
  // Update phase
  (0 until UpdateSteps) foreach { i =>
    val (updateTime, _) = time(updateDynRH(UpdateDocumentsNumber))

    val (searchTime, _) = time(searchDynRH(KeywordSearches))

    println(s"${StartDocumentsNumber + UpdateDocumentsNumber * i},$updateTime,${searchTime / KeywordSearches}")
  }


  private def initialize(docNumber: Int): util.HashMap[String, Array[Byte]] = {
    // Initialize database with 10k documents
    val startDir: File = createTempDirForPrefix("startDir")
    val startDirPath: String = startDir.getAbsolutePath
    println(s"\nBeginning of Encrypted Multi-map creation in $startDirPath \n")

    (0 until docNumber) foreach { _ =>
      createFileWithContent(startDir, sentenceGen.sample.get)
    }
    // parse the directory with files
    TextProc.listf(startDirPath, new util.ArrayList[File])
    TextProc.TextProc(false, startDirPath)

    // Construction of the encrypted multi-map
    // This operation will simply generate a dictionary on the server side
    // The setup will be performed as multiple update operations
    val emm = DynRH.setup()
    // Generate the updates
    // This operation will generate update tokens for the entire data set
    val tokenUp = DynRH.tokenUpdate(sk, TextExtractPar.lp1)
    // Update the encrypted Multi-map
    // the dictionary is updated on the server side
    DynRH.update(emm, tokenUp)
    println(s"Created ${TextExtractPar.lp1.keySet.size} keywords and ${TextExtractPar.lp1.keys.size} pairs " +
      s"in $initTime ms")
    emm
  }

  def updateDynRH(updateDocumentsNumber: Int): Unit = {
    // Empty the previous multimap
    // to avoid adding the same set of documents for every update
    TextExtractPar.lp1 = ArrayListMultimap.create()
    val updateDir: File = createTempDirForPrefix("dir2")
    val updateDirPath: String = updateDir.getAbsolutePath
    (0 until updateDocumentsNumber) foreach { _ =>
      createFileWithContent(updateDir, sentenceGen.sample.get)
    }
    TextProc.listf(updateDirPath, new util.ArrayList[File])
    TextProc.TextProc(false, updateDirPath)
    // This operation is similar to the one performed above
    val updateToken = DynRH.tokenUpdate(sk, TextExtractPar.lp1)
    DynRH.update(emm, updateToken)
  }

  def searchDynRH(iterations: Int): Int = {
    val results = (0 until iterations) map { _ =>
      val keyword = keywordGen.sample.get
      val token = DynRH.genTokenFS(sk, keyword)
      val result = DynRH.resolve(sk, DynRH.queryFS(token, emm))
      result.size()
    }
    results.sum
  }

}
