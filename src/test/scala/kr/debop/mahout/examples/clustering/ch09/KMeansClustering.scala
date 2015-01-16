package kr.debop.mahout.examples.clustering.ch09

import kr.debop.mahout.examples.AbstractMahoutFunSuite
import kr.debop.mahout.examples.clustering.ClusterHelper
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.mahout.clustering.kmeans.{KMeansDriver, RandomSeedGenerator}
import org.apache.mahout.common.HadoopUtil
import org.apache.mahout.common.distance.CosineDistanceMeasure

import scala.collection.JavaConverters._

/**
 * KMeansClustering
 * @author sunghyouk.bae@gmail.com
 */
class KMeansClustering extends AbstractMahoutFunSuite {

  test("K-Means Clustering") {
    val inputDir = "reuters-vectors"

    val conf = new Configuration()
    val vectorsFolder = inputDir + "/tfidf-vectors"
    val samples = new Path(vectorsFolder + "/part-r-00000")
    val output = new Path("output")
    HadoopUtil.delete(conf, output)
    val measure = new CosineDistanceMeasure()

    val clustersIn = new Path(output, "random-seeds")
    RandomSeedGenerator.buildRandom(conf, samples, clustersIn, 3, measure)
    KMeansDriver.run(conf, samples, clustersIn, output, 0.01, 10, true, 0.0, true)

    val clusters = ClusterHelper.readClusters(conf, output)

    clusters.get(clusters.size - 1).asScala.foreach { cluster =>
      println(s"Cluster id:${cluster.getId} cender:${cluster.getCenter.asFormatString()}")
    }
  }

}
