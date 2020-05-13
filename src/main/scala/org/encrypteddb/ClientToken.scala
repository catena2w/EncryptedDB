package org.encrypteddb

import com.google.common.collect.TreeMultimap

sealed trait ClientToken

sealed trait ModificationToken

case class SearchToken(token: Array[Array[Byte]], key2: Array[Byte]) extends ClientToken

case class InsertToken(token: TreeMultimap[String, Array[Byte]]) extends ModificationToken

case class DeleteToken(key1: Array[Byte], deletions: List[Int]) extends ModificationToken
