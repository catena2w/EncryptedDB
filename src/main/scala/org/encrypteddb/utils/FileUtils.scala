package org.encrypteddb.utils

import java.io.{File, IOException, PrintWriter}
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, Files, Path, SimpleFileVisitor}

import scala.util.Try

/**
  * Trait is used to satisfy Clusion interfaces, that requires files on input.
  * It generates files in temp directory, and delete the on exit
  */
trait FileUtils {

  protected val basePath: Path = java.nio.file.Files.createTempDirectory(s"encrdb-${System.nanoTime()}")

  sys.addShutdownHook {
    remove(basePath)
  }

  protected def createFileWithContent(dir: java.io.File, fileName: String, content: String): Try[java.io.File] = Try {
    val file = new File(dir.getAbsolutePath + File.separator + fileName)
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
