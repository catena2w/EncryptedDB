name := "EncryptedDB"

version := "0.1.0"

scalaVersion := "2.12.4"

unmanagedBase := baseDirectory.value / "libs"

val poiVersion = "3.15-beta1"

libraryDependencies ++= Seq(

  // test libraries
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "org.scalatest" %% "scalatest" % "3.0.+" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.+" % "test",

  // Clusion and its's dependencies
  "org.crypto.sse" % "Clusion" % "1.0-SNAPSHOT" from s"file://${baseDirectory.value}/project/libs/Clusion-1.0-SNAPSHOT.jar",
  "org.bouncycastle" % "bcprov-jdk15on" % "1.54",
  "org.apache.lucene" % "lucene-core" % "7.4.0",
  "org.apache.pdfbox" % "pdfbox" % "2.0.15",
  "org.mapdb" % "mapdb" % "3.0.4",
  "com.google.guava" % "guava" % "19.0",
  "com.carrotsearch" % "java-sizeof" % "0.0.5",
  "com.amazonaws" % "aws-java-sdk" % "1.11.19",
  "org.apache.hadoop" % "hadoop-core" % "1.2.1",
  "org.apache.hadoop" % "hadoop-common" % "2.7.1",
  "org.apache.lucene" % "lucene-analyzers-common" % "7.4.0",
  "org.apache.poi" % "poi" % poiVersion,
  "org.apache.poi" % "poi-scratchpad" % poiVersion,
  "org.apache.poi" % "poi-ooxml" % poiVersion,
)
