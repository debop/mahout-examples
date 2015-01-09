package kr.debop.mahout.examples.recommender.ch06

import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.mapreduce.Mapper
import org.apache.mahout.math.{VarLongWritable, VectorWritable}

/**
 * UserVectorToConcurrenceMapper
 * @author sunghyouk.bae@gmail.com
 */
class UserVectorToConcurrenceMapper
  extends Mapper[VarLongWritable, VectorWritable, IntWritable, IntWritable] {

  override def map(key: VarLongWritable,
                   value: VectorWritable,
                   context: Mapper[VarLongWritable, VectorWritable, IntWritable, IntWritable]#Context): Unit = {
    val it = value.get().nonZeroes().iterator()
    while (it.hasNext) {
      val index1 = it.next().index()
      val it2 = value.get.nonZeroes().iterator()
      while (it2.hasNext) {
        val index2 = it2.next().index()
        context.write(new IntWritable(index1), new IntWritable(index2))
      }
    }
  }
}
