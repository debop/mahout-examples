package kr.debop.mahout.examples.clustering.ch10

import java.util

import org.apache.hadoop.conf.Configuration
import org.apache.mahout.common.distance.DistanceMeasure
import org.apache.mahout.common.parameters.Parameter
import org.apache.mahout.math

/**
 * MyDistanceMeasure
 * @author sunghyouk.bae@gmail.com
 */
class MyDistanceMeasure extends DistanceMeasure {

  override def distance(v1: math.Vector, v2: math.Vector): Double = ???
  override def distance(centroidLengthSquare: Double, centroid: math.Vector, v: math.Vector): Double = ???
  override def configure(config: Configuration): Unit = ???
  override def getParameters: util.Collection[Parameter[_]] = ???
  override def createParameters(prefix: String, jobConf: Configuration): Unit = ???
}
