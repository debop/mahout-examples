package kr.debop.mahout.examples.clustering.ch12.lastfm

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path

/**
 * VectorCreationJob
 * @author sunghyouk.bae@gmail.com
 */
object VectorCreationJob {

  def createVectors(input: Path, output: Path, dictionaryPath: Path): Unit = {

  }

  def generateDictionary(input: Path, output: Path): Unit = {

  }

  private def createNewConfiguration(): Configuration = {
    val conf = new Configuration()

    conf.set("mapred.compress.map.output", "true")
    conf.set("mapred.output.compression.type", "BLOCK")
    conf.set("io.serializations",
              "org.apache.hadoop.io.serializer.JavaSerialization, " +
              "org.apache.hadoop.io.serializer.WritableSerialization")

    conf
  }

}
