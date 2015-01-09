package kr.debop.mahout.examples.recommender.ch06

import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.mapreduce.Mapper
import org.apache.mahout.cf.taste.hadoop.item.VectorAndPrefsWritable
import org.apache.mahout.math.{VarLongWritable, VectorWritable}

/**
 * PartialMultiplyMapper
 * @author sunghyouk.bae@gmail.com
 */
class PartialMultiplyMapper extends Mapper[IntWritable, VectorAndPrefsWritable, VarLongWritable, VectorWritable] {

  override def map(key: IntWritable,
                   value: VectorAndPrefsWritable,
                   context: Mapper[IntWritable, VectorAndPrefsWritable, VarLongWritable, VectorWritable]#Context): Unit = {
    val concurrenceColumn = value.getVector
    val userIDs = value.getUserIDs
    val prefValues = value.getValues

    for (i <- 0 until userIDs.size()) {
      val userID = userIDs.get(i)
      val prefValue = prefValues.get(i)
      val partialProduct = concurrenceColumn.times(prefValue.toDouble)
      context.write(new VarLongWritable(userID),
                     new VectorWritable(partialProduct))
    }
  }
}
