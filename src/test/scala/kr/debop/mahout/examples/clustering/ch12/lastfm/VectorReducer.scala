package kr.debop.mahout.examples.clustering.ch12.lastfm

import java.lang.Iterable

import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Reducer
import org.apache.mahout.math
import org.apache.mahout.math.function.Functions
import org.apache.mahout.math.{NamedVector, VectorWritable}

import scala.collection.JavaConverters._

/**
 * VectorReducer
 * @author sunghyouk.bae@gmail.com
 */
class VectorReducer extends Reducer[Text, VectorWritable, Text, VectorWritable] {

  val writer = new VectorWritable()

  override def reduce(tag: Text,
                      values: Iterable[VectorWritable],
                      context: Reducer[Text, VectorWritable, Text, VectorWritable]#Context): Unit = {
    var vector: math.Vector = null

    values.asScala.foreach { partialVector =>
      if (vector == null) {
        vector = partialVector.get().like()
      }
      partialVector.get.assign(vector, Functions.PLUS)
    }
    val namedVector = new NamedVector(vector, tag.toString)
    writer.set(namedVector)
    context.write(tag, writer)
  }
}
