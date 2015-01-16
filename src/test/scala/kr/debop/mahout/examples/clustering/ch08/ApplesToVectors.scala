package kr.debop.mahout.examples.clustering.ch08

import java.util

import kr.debop.mahout.examples.AbstractMahoutFunSuite
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.{SequenceFile, Text}
import org.apache.mahout.math.{DenseVector, NamedVector, VectorWritable}

import scala.collection.JavaConverters._

/**
 * ApplesToVectors
 * @author sunghyouk.bae@gmail.com
 */
class ApplesToVectors extends AbstractMahoutFunSuite {

  test("Apples to Vectors") {
    val apples = new util.ArrayList[NamedVector]()

    apples.add(new NamedVector(new DenseVector(Array[Double](0.11, 510, 1)), "Small round green apple"))
    apples.add(new NamedVector(new DenseVector(Array[Double](0.23, 650, 3)), "Large oval red apple"))
    apples.add(new NamedVector(new DenseVector(Array[Double](0.09, 630, 1)), "Small elongated red apple"))
    apples.add(new NamedVector(new DenseVector(Array[Double](0.25, 590, 3)), "Large round yellow apple"))
    apples.add(new NamedVector(new DenseVector(Array[Double](0.18, 520, 2)), "Medium oval green apple"))

    val conf = new Configuration()
    val fs = FileSystem.get(conf)

    val path = new Path("appledata/apples")
    // val writer = new SequenceFile.Writer(fs, conf, path, classOf[Text], classOf[VectorWritable])
    val writer = SequenceFile.createWriter(fs, conf, path, classOf[Text], classOf[VectorWritable])
    val vec = new VectorWritable()

    apples.asScala.foreach { vector =>
      vec.set(vector)
      writer.append(new Text(vector.getName), vec)
    }
    writer.close()

    val reader = new SequenceFile.Reader(fs, path, conf)

    val key = new Text()
    val value = new VectorWritable()
    while (reader.next(key, value)) {
      println(s"$key ${value.get().asFormatString()}")
    }
    reader.close()
  }
}
