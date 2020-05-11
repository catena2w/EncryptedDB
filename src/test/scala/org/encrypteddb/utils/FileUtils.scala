package org.encrypteddb.utils

import java.io.{IOException, PrintWriter}
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, Files, Path, SimpleFileVisitor}

import scala.util.Try

trait FileUtils {

  protected val randomPrefixLength = 10

  val basePath: Path = java.nio.file.Files.createTempDirectory(s"encrdb-${System.nanoTime()}")

  sys.addShutdownHook {
    remove(basePath)
  }

  def createFileWithContent(dir: java.io.File, content: String): Try[java.io.File] = Try {
    val fileName = scala.util.Random.alphanumeric.take(randomPrefixLength).mkString
    val file = java.nio.file.Files.createTempFile(dir.toPath, fileName, ".tmp").toFile
    val pw = new PrintWriter(file)
    pw.write(content)
    pw.close()
    file
  }

  /**
    * Recursively remove all files and directories in `root`
    */
  private def remove(root: Path): Unit = {
    Files.walkFileTree(root, new SimpleFileVisitor[Path] {
      override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
        Files.delete(file)
        FileVisitResult.CONTINUE
      }

      override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = {
        Files.delete(dir)
        FileVisitResult.CONTINUE
      }
    })
  }

  def createTempDirForPrefix(prefix: String): java.io.File = {
    java.nio.file.Files.createTempDirectory(basePath, prefix).toFile
  }

}
