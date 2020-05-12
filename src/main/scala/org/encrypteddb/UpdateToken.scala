package org.encrypteddb

import com.google.common.collect.TreeMultimap

case class UpdateToken(token: TreeMultimap[String, Array[Byte]])
