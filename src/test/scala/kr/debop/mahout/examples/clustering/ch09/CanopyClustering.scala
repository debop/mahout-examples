package kr.debop.mahout.examples.clustering.ch09

import java.util

import kr.debop.mahout.examples.AbstractMahoutFunSuite
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.{SequenceFile, Text}
import org.apache.mahout.clustering.canopy.CanopyClusterer
import org.apache.mahout.clustering.kmeans.Kluster
import org.apache.mahout.common.distance.CosineDistanceMeasure
import org.apache.mahout.math.{Vector => Vec, VectorWritable}

import scala.collection.JavaConverters._

/**
 * CanopyClustering
 * @author sunghyouk.bae@gmail.com
 */
class CanopyClustering extends AbstractMahoutFunSuite {

  test("Canopy Clustering") {
    val inputDir = "reuters"

    val conf = new Configuration()
    val fs = FileSystem.get(conf)
    val vectorsFolder = inputDir + "/tfidf-vectors"

    val reader = new SequenceFile.Reader(fs, new Path(vectorsFolder + "/part-r-00000"), conf)
    val points = new util.ArrayList[Vec]()
    val key = new Text()
    val value = new VectorWritable()

    while (reader.next(key, value)) {
      points.add(value.get)
    }
    reader.close()
    println(s"Point size=${points.size()}")

    val canopies = CanopyClusterer.createCanopies(points, new CosineDistanceMeasure(), 0.8, 0.7)
    val clusters = new util.ArrayList[Kluster]()
    println(s"Canopy size=${canopies.size()}")
    canopies.asScala.foreach { canopy =>
      clusters.add(new Kluster(canopy.getCenter, canopy.getId, new CosineDistanceMeasure))
    }
    canopies.asScala.foreach {println}
  }

}
