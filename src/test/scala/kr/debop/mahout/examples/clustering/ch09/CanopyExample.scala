package kr.debop.mahout.examples.clustering.ch09

import java.util

import kr.debop.mahout.examples.AbstractMahoutFunSuite
import org.apache.mahout.clustering.canopy.CanopyClusterer
import org.apache.mahout.common.distance.EuclideanDistanceMeasure
import org.apache.mahout.math.{Vector => Vec}

import scala.collection.JavaConverters._

/**
 * CanopyExample
 * @author sunghyouk.bae@gmail.com
 */
class CanopyExample extends AbstractMahoutFunSuite {

  test("Canopy example") {
    val sampleData = new util.ArrayList[Vec]()

    RandomPointsUtil.generateSamples(sampleData, 400, 1, 1, 2)
    RandomPointsUtil.generateSamples(sampleData, 300, 1, 0, 0.5)
    RandomPointsUtil.generateSamples(sampleData, 300, 0, 2, 0.1)

    val canopies = CanopyClusterer.createCanopies(sampleData,
                                                   new EuclideanDistanceMeasure(),
                                                   3.0,
                                                   2.2)
    canopies.asScala.foreach { canopy =>
      println(s"Canopy id:${canopy.getId} center:${canopy.getCenter.asFormatString()}")
    }
  }

}
