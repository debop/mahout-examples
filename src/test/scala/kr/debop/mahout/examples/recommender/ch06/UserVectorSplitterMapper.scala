package kr.debop.mahout.examples.recommender.ch06

import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.mapreduce.Mapper
import org.apache.mahout.cf.taste.hadoop.item.VectorOrPrefWritable
import org.apache.mahout.math.{VarLongWritable, VectorWritable}

/**
 * UserVectorSplitterMapper
 * @author sunghyouk.bae@gmail.com
 */
class UserVectorSplitterMapper
  extends Mapper[VarLongWritable, VectorWritable, IntWritable, VectorOrPrefWritable] {

  override def map(key: VarLongWritable,
                   value: VectorWritable,
                   context: Mapper[VarLongWritable, VectorWritable, IntWritable, VectorOrPrefWritable]#Context): Unit = {
    val userID = key.get()
    val userVector = value.get()
    val itemIndexWritable = new IntWritable()
    val it = userVector.nonZeroes().iterator()
    while (it.hasNext) {
      val e = it.next()
      val itemIndex = e.index()
      val preferenceValue = e.get().toFloat

      itemIndexWritable.set(itemIndex)
      context.write(itemIndexWritable, new VectorOrPrefWritable(userID, preferenceValue))
    }
  }
}
