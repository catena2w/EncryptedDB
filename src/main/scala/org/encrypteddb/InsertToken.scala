package org.encrypteddb

import com.google.common.collect.TreeMultimap

case class InsertToken(token: TreeMultimap[String, Array[Byte]])
