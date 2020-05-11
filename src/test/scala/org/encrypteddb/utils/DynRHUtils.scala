package org.encrypteddb.utils

import java.io.File
import java.util

import com.google.common.collect.ArrayListMultimap
import org.crypto.sse.{DynRH, TextExtractPar, TextProc}

import scala.collection.JavaConverters._

trait DynRHUtils extends FileUtils with WordUtils {

  def initializeDynRH(sk: Array[Byte], docNumber: Int): util.HashMap[String, Array[Byte]] = {
    // Initialize database with 10k documents
    val startDir: File = createTempDirForPrefix("startDir")
    val startDirPath: String = startDir.getAbsolutePath
    println(s"\nBeginning of Encrypted Multi-map creation in $startDirPath \n")

    (0 until docNumber) foreach { _ =>
      createFileWithContent(startDir, docGen.sample.get)
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
    println(s"Created ${TextExtractPar.lp1.keySet.size} keywords and ${TextExtractPar.lp1.keys.size} pairs ")
    emm
  }

  def updateDynRH(sk: Array[Byte], emm: util.HashMap[String, Array[Byte]], docs: Seq[String]): Unit = {
    // Empty the previous multimap
    // to avoid adding the same set of documents for every update
    TextExtractPar.lp1 = ArrayListMultimap.create()
    val updateDir: File = createTempDirForPrefix("dir2")
    val updateDirPath: String = updateDir.getAbsolutePath
    docs foreach { doc =>
      createFileWithContent(updateDir, doc)
    }
    TextProc.listf(updateDirPath, new util.ArrayList[File])
    TextProc.TextProc(false, updateDirPath)
    val updateToken = DynRH.tokenUpdate(sk, TextExtractPar.lp1)
    DynRH.update(emm, updateToken)
  }


  def searchDynRH(sk: Array[Byte], emm: util.HashMap[String, Array[Byte]], keyword: String): List[String] = {
    val token = DynRH.genTokenFS(sk, keyword)
    DynRH.resolve(sk, DynRH.queryFS(token, emm)).asScala.toList
  }

}
