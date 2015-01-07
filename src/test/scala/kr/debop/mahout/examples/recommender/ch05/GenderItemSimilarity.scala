package kr.debop.mahout.examples.recommender.ch05

import java.util

import org.apache.mahout.cf.taste.common.Refreshable
import org.apache.mahout.cf.taste.impl.common.FastIDSet
import org.apache.mahout.cf.taste.similarity.ItemSimilarity

/**
 * GenreItemSimilarity
 * @author sunghyouk.bae@gmail.com
 */
class GenderItemSimilarity(val men: FastIDSet, val women: FastIDSet) extends ItemSimilarity {

  override def itemSimilarity(itemID1: Long, itemID2: Long): Double = {
    val profile1IsMan = isMan(itemID1)
    if (profile1IsMan == None) return 0.0

    val profile2IsMan = isMan(itemID2)
    if (profile2IsMan == None) return 0.0

    if (profile1IsMan == profile2IsMan) 1.0
    else -1.0
  }

  override def itemSimilarities(itemID1: Long, itemID2s: Array[Long]): Array[Double] = {
    val result = new Array[Double](itemID2s.length)
    for (x <- 0 until itemID2s.length) {
      result(x) = itemSimilarity(itemID1, itemID2s(x))
    }
    result
  }

  override def allSimilarItemIDs(itemID: Long): Array[Long] =
    throw new UnsupportedOperationException()

  override def refresh(alreadyRefreshed: util.Collection[Refreshable]): Unit = {
    // nothing to do.
  }

  private def isMan(profileId: Long): Option[Boolean] = {
    if (men.contains(profileId))
      return Some(true)
    if (women.contains(profileId))
      return Some(false)
    None
  }
}
