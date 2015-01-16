package kr.debop.mahout.examples.clustering.ch09

import java.io.File
import java.util

import kr.debop.mahout.examples.AbstractMahoutFunSuite
import kr.debop.mahout.examples.clustering.ClusterHelper
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.mahout.clustering.kmeans.{KMeansDriver, RandomSeedGenerator}
import org.apache.mahout.common.HadoopUtil
import org.apache.mahout.common.distance.EuclideanDistanceMeasure
import org.apache.mahout.math.{Vector => Vec}

import scala.collection.JavaConverters._

/**
 * KMeansExample
 * @author sunghyouk.bae@gmail.com
 */
class KMeansExample extends AbstractMahoutFunSuite {

  test("K-Means Example") {
    val sampleData = new util.ArrayList[Vec]()

    RandomPointsUtil.generateSamples(sampleData, 400, 1, 1, 3)
    RandomPointsUtil.generateSamples(sampleData, 300, 1, 0, 0.5)
    RandomPointsUtil.generateSamples(sampleData, 300, 0, 2, 0.1)

    val k = 3
    val testData = new File("input")
    if (!testData.exists()) {
      testData.mkdir()
    }

    val conf = new Configuration()
    val samples = new Path("input/file1")
    ClusterHelper.writePointsToFile(sampleData, conf, samples)

    val output = new Path("output")
    HadoopUtil.delete(conf, output)

    val clustersIn = new Path(output, "random-seeds")
    val measure = new EuclideanDistanceMeasure()

    RandomSeedGenerator.buildRandom(conf, samples, clustersIn, k, measure)
    KMeansDriver.run(conf, samples, clustersIn, output, 0.01, 10, true, 0.0, true)

    val Clusters = ClusterHelper.readClusters(conf, output)
    Clusters.get(Clusters.size - 1).asScala.foreach { cluster =>
      println(s"Cluster id:${cluster.getId} center:${cluster.getCenter.asFormatString()}")
    }
  }

}
