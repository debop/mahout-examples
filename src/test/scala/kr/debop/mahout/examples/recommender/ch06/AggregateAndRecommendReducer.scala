package kr.debop.mahout.examples.recommender.ch06

import java.lang.Iterable
import java.util
import java.util.Collections

import kr.debop.mahout.examples.recommender.ch06.AggregateAndRecommendReducer._
import org.apache.hadoop.mapreduce.Reducer
import org.apache.mahout.cf.taste.hadoop.{RecommendedItemsWritable, TasteHadoopUtils}
import org.apache.mahout.cf.taste.impl.recommender.{ByValueRecommendedItemComparator, GenericRecommendedItem}
import org.apache.mahout.cf.taste.recommender.RecommendedItem
import org.apache.mahout.math.map.OpenIntLongHashMap
import org.apache.mahout.math.{VarLongWritable, Vector, VectorWritable}

import scala.collection.JavaConverters._

/**
 * AggregateAndRecommendReduce
 * @author sunghyouk.bae@gmail.com
 */
class AggregateAndRecommendReducer
  extends Reducer[VarLongWritable, VectorWritable, VarLongWritable, RecommendedItemsWritable] {

  private var recommendationsPerUser = 10
  private var indexItemIDMap: OpenIntLongHashMap = _


  override def setup(context: Reducer[VarLongWritable, VectorWritable, VarLongWritable, RecommendedItemsWritable]#Context): Unit = {
    val jobConf = context.getConfiguration
    recommendationsPerUser = jobConf.getInt(NUM_RECOMMENDATIONS, DEFAULT_NUM_RECOMMENDATIONS)
    indexItemIDMap = TasteHadoopUtils.readIDIndexMap(jobConf.get(ITEMID_INDEX_PATH), jobConf)
  }

  override def reduce(key: VarLongWritable,
                      values: Iterable[VectorWritable],
                      context: Reducer[VarLongWritable, VectorWritable, VarLongWritable, RecommendedItemsWritable]#Context): Unit = {
    var recommendationVector: Vector = null
    values.asScala.foreach { vectorWritable =>
      recommendationVector =
        if (recommendationVector == null) vectorWritable.get()
        else recommendationVector.plus(vectorWritable.get())
    }
    val topItems = new util.PriorityQueue[RecommendedItem](recommendationsPerUser + 1,
                                                            Collections.reverseOrder(ByValueRecommendedItemComparator.getInstance()))
    val recommendationVectorIter = recommendationVector.nonZeroes().iterator()
    while (recommendationVectorIter.hasNext) {
      val element = recommendationVectorIter.next()
      val index = element.index()
      val value = element.get().toFloat
      if (topItems.size < recommendationsPerUser) {
        topItems.add(new GenericRecommendedItem(indexItemIDMap.get(index), value))
      } else if (value > topItems.peek().getValue) {
        topItems.add(new GenericRecommendedItem(indexItemIDMap.get(index), value))
        topItems.poll()
      }
    }

    val recommendations = new util.ArrayList[RecommendedItem](topItems.size)
    recommendations.addAll(topItems)
    Collections.sort(recommendations, ByValueRecommendedItemComparator.getInstance())
    context.write(key, new RecommendedItemsWritable(recommendations))

  }
}

object AggregateAndRecommendReducer {
  val ITEMID_INDEX_PATH = "itemIDIndexPath"
  val NUM_RECOMMENDATIONS = "numRecommendations"
  val DEFAULT_NUM_RECOMMENDATIONS = 10
}
