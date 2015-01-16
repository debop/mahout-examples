package kr.debop.mahout.examples.clustering.ch07

import java.io.File
import java.util

import kr.debop.mahout.examples.AbstractMahoutFunSuite
import kr.debop.mahout.examples.clustering.ClusterHelper
import kr.debop.mahout.examples.clustering.ch07.SimpleKMeansClustering._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.{IntWritable, SequenceFile, Text}
import org.apache.mahout.clustering.Cluster
import org.apache.mahout.clustering.classify.WeightedVectorWritable
import org.apache.mahout.clustering.kmeans.{KMeansDriver, Kluster}
import org.apache.mahout.common.HadoopUtil
import org.apache.mahout.common.distance.EuclideanDistanceMeasure
import org.apache.mahout.math.{RandomAccessSparseVector, Vector}

/**
 * SimpleKMeansClustering
 * @author sunghyouk.bae@gmail.com
 */
class SimpleKMeansClusteringFunSuite extends AbstractMahoutFunSuite {

  test("Simple K-Means Clustering") {
    val k = 2
    val vectors = getPoints(points)

    val testData = new File("testData")
    if (!testData.exists()) {
      testData.mkdir()
    }

    val conf = new Configuration()
    val fs = FileSystem.get(conf)
    ClusterHelper.writePointsToFile(vectors, conf, new Path("testdata/points/file1"))

    val path = new Path("testdata/clusters/part-00000")
    val writer = SequenceFile.createWriter(fs, conf, path, classOf[Text], classOf[Kluster])

    for (i <- 0 until k) {
      val vec = vectors.get(i)
      val cluster = new Kluster(vec, i, new EuclideanDistanceMeasure())
      writer.append(new Text(cluster.getIdentifier), cluster)
    }
    writer.close()

    val output = new Path("output")
    HadoopUtil.delete(conf, output)

    KMeansDriver.run(conf, new Path("testdata/points"), new Path("testdata/clusters"), output, 0.001, 10, true, 0.0, false)

    val reader = new SequenceFile.Reader(fs, new Path("output/" + Cluster.CLUSTERED_POINTS_DIR + "/part-m-00000"), conf)

    val key = new IntWritable()
    val value = new WeightedVectorWritable()
    while (reader.next(key, value)) {
      println(s"$value belongs to cluster $key")
    }
    reader.close()
  }

}

object SimpleKMeansClustering {
  val points: Array[Array[Double]] = Array(Array(1, 1), Array(2, 1), Array(1, 2),
                                            Array(2, 2), Array(3, 3), Array(8, 8),
                                            Array(9, 8), Array(8, 9), Array(9, 9))
  def getPoints(raw: Array[Array[Double]]): util.List[Vector] = {
    val points = new util.ArrayList[Vector]()
    for (i <- 0 until raw.length) {
      val fr = raw(i)
      val vec = new RandomAccessSparseVector(fr.length)
      vec.assign(fr)
      points.add(vec)
    }
    points
  }
}
