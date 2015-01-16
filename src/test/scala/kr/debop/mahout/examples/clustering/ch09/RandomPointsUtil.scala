package kr.debop.mahout.examples.clustering.ch09

import java.{lang, util}

import org.apache.mahout.clustering.UncommonDistributions
import org.apache.mahout.common.RandomUtils
import org.apache.mahout.math.{DenseVector, Vector => Vec}

import scala.collection.JavaConverters._

/**
 * RandomPointsUtil
 * @author sunghyouk.bae@gmail.com
 */
object RandomPointsUtil {

  def generateSamples(vectors: util.List[Vec], num: Int, mx: Double, my: Double, sd: Double): Unit = {
    for (i <- 0 until num) {
      vectors.add(new DenseVector(Array[Double](UncommonDistributions.rNorm(mx, sd),
                                                 UncommonDistributions.rNorm(my, sd))))
    }
  }

  def chooseRandomPoints(vectors: lang.Iterable[Vec], k: Int): util.List[Vec] = {
    val chosenPoints = new util.ArrayList[Vec](k)
    val random = RandomUtils.getRandom

    vectors.asScala.foreach { value =>
      val currentSize = chosenPoints.size()
      if (currentSize < k) {
        chosenPoints.add(value)
      } else if (random.nextInt(currentSize + 1) == 0) {
        // with chance 1/(currentSize+1) pick new element
        val indexToRemove = random.nextInt(currentSize) // evict one chosen randomly
        chosenPoints.remove(indexToRemove)
        chosenPoints.add(value)
      }
    }
    chosenPoints
  }
}
