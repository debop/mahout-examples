package kr.debop.mahout.examples.clustering.ch07

import java.util

import kr.debop.mahout.examples.AbstractMahoutFunSuite
import org.apache.mahout.math.{RandomAccessSparseVector, Vector}

/**
 * SimpleKMeansClustering
 * @author sunghyouk.bae@gmail.com
 */
class SimpleKMeansClusteringFunSuite extends AbstractMahoutFunSuite {


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
