package kr.debop.mahout.examples.clustering.ch09

import kr.debop.mahout.examples.AbstractMahoutFunSuite
import kr.debop.mahout.examples.clustering.ClusterHelper
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.mahout.clustering.canopy.CanopyDriver
import org.apache.mahout.clustering.kmeans.KMeansDriver
import org.apache.mahout.common.HadoopUtil
import org.apache.mahout.common.distance.CosineDistanceMeasure

import scala.collection.JavaConverters._

/**
 * KMeansWithCanopyClustering
 * @author sunghyouk.bae@gmail.com
 */
class KMeansWithCanopyClustering extends AbstractMahoutFunSuite {

  test("K-Means with Canopy Clustering") {

    val inputDir = "reuters"

    val conf = new Configuration
    val vectorsFolder = new Path(inputDir, "tfidf-vectors")
    val samples = new Path(vectorsFolder, "part-r-00000")

    val output = new Path("output")
    HadoopUtil.delete(conf, output)

    val canopyCentroids = new Path(output, "canopy-centroids")
    val clusterOutput = new Path(output, "clusters")

    CanopyDriver.run(conf, samples, canopyCentroids, new CosineDistanceMeasure, 0.7, 0.5, false, 0, false)
    KMeansDriver.run(conf, vectorsFolder, new Path(canopyCentroids, "clusters-0-final"), clusterOutput, 0.01, 20, true, 0.0, false)

    val Clusters = ClusterHelper.readClusters(conf, clusterOutput)
    Clusters.asScala.last.asScala.foreach { cluster =>
      println(s"Cluster id:${cluster.getId} center:${cluster.getCenter.asFormatString()}")
    }
  }

}
