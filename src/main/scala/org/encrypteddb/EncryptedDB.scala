package org.encrypteddb

import java.io.File
import java.util

import com.google.common.collect.ArrayListMultimap
import com.typesafe.scalalogging.StrictLogging
import org.crypto.sse._
import org.encrypteddb.utils.FileUtils

import scala.collection.JavaConverters._
import scala.util.{Random, Try}

/**
  *
  *
  * @param sk - secret key
  */
class EncryptedDB(sk: Array[Byte]) extends FileUtils with StrictLogging {

  private var emm = DynRH.setup()

  /**
    * Parse all documents from provided directory and add them to search indexes
    *
    * @param dir - directory with documents to insert
    * @return Success on successful update, Failure otherwise
    */
  def insert(dir: File): Try[Unit] = Try {
    val fileList = new util.ArrayList[File]
    TextProc.listf(dir.getAbsolutePath, fileList)
    TextProc.TextProc(false, dir.getAbsolutePath)
    val updateToken = DynRH.tokenUpdate(sk, TextExtractPar.lp1)
    DynRH.update(emm, updateToken)
    logger.info(s"Inserted ${fileList.size()} documents with ${TextExtractPar.lp1.keySet.size} keywords and ${TextExtractPar.lp1.keys.size} pairs ")
  }

  /**
    * Add provided documents to search indexes
    *
    * @param documents - array of id->content tuples
    * @return Success on successful update, Failure otherwise
    */
  def insert(documents: Seq[(String, String)]): Try[Unit] = Try {
    // Empty the previous multimap to avoid adding the same set of documents for every update
    TextExtractPar.lp1 = ArrayListMultimap.create()
    // put documents to temp files to satisfy Clusion interfaces
    val insertDir: File = createTempDirForPrefix(Random.alphanumeric.take(10).mkString)
    documents foreach { doc =>
      createFileWithContent(insertDir, doc._1, doc._2)
    }
    insert(insertDir).get
  }

  /**
    * Find all documents with the provided keyword
    *
    * @param keyword
    * @return list of document ids
    */
  def search(keyword: String): List[String] = {
    val token = DynRH.genTokenFS(sk, keyword)
    DynRH.resolve(sk, DynRH.queryFS(token, emm)).asScala.toList
  }

}

object EncryptedDB {
  def create(documents: Seq[(String, String)]): EncryptedDB = {
    val sk: Array[Byte] = RR2Lev.keyGen(256, Random.nextString(256), "salt/salt", 100000)
    val db = new EncryptedDB(sk)
    db.insert(documents)
    db
  }
}
