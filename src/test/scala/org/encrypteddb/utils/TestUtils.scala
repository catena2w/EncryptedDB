package org.encrypteddb.utils

trait TestUtils extends FileUtils with WordUtils {

  def time[R](block: => R): (Float, R) = {
    val t0 = System.nanoTime()
    val result = block // call-by-name
    val t1 = System.nanoTime()
    ((t1 - t0).toFloat / 1000000, result)
  }

}
