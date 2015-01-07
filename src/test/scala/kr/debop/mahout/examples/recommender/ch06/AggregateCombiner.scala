package kr.debop.mahout.examples.recommender.ch06

import java.lang.Iterable

import org.apache.hadoop.mapreduce.Reducer
import org.apache.mahout.math.{VarLongWritable, Vector => MVector, VectorWritable}

import scala.collection.JavaConverters._

/**
 * AggregateCombiner
 * @author sunghyouk.bae@gmail.com
 */
class AggregateCombiner extends Reducer[VarLongWritable, VectorWritable, VarLongWritable, VectorWritable] {
  override def reduce(key: VarLongWritable,
                      values: Iterable[VectorWritable],
                      context: Reducer[VarLongWritable, VectorWritable, VarLongWritable, VectorWritable]#Context) {
    var partial: MVector = null
    values.asScala.foreach { vectorWritable =>
      partial = if (partial == null) vectorWritable.get() else partial.plus(vectorWritable.get())
      context.write(key, new VectorWritable(partial))
    }
  }
}
