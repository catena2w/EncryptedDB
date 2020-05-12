package org.encrypteddb

import com.typesafe.scalalogging.StrictLogging
import org.crypto.sse._
import org.encrypteddb.utils.FileUtils

import scala.collection.JavaConverters._
import scala.util.Try

class EDBServer extends FileUtils with StrictLogging {

  private var emm = DynRH.setup()

  def insert(updateToken: UpdateToken): Try[Unit] = Try {
    DynRH.update(emm, updateToken.token)
    logger.info(s"Server database updated")
  }


  /**
    * Find all documents with the provided keyword
    *
    * @param keyword
    * @return list of document ids
    */
  def search(token: SearchToken): List[String] = {
    DynRH.resolve(token.key2, DynRH.queryFS(token.token, emm)).asScala.toList
  }

}
