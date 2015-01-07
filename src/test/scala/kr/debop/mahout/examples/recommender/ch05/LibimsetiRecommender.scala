package kr.debop.mahout.examples.recommender.ch05

import java.io.File
import java.util

import org.apache.mahout.cf.taste.common.Refreshable
import org.apache.mahout.cf.taste.impl.common.FastIDSet
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity
import org.apache.mahout.cf.taste.model.DataModel
import org.apache.mahout.cf.taste.recommender.{IDRescorer, RecommendedItem, Recommender}

/**
 * LibimsetiRecommender
 * @author sunghyouk.bae@gmail.com
 */
class LibimsetiRecommender(val model: DataModel) extends Recommender {

  def this() = this(new FileDataModel(new File("src/data/libimseti/ratings.dat")))

  val similarity = new EuclideanDistanceSimilarity(model)
  val neighborhood = new NearestNUserNeighborhood(2, similarity, model)
  val delegate = new GenericUserBasedRecommender(model, neighborhood, similarity)
  val menWomen = GenderRescorer.parseMenWomen(new File("src/data/libimseti/gender.dat"))
  val men = menWomen(0)
  val women = menWomen(1)
  val usersRateMoreMen = new FastIDSet(5000)
  val usersRateLessMen = new FastIDSet(5000)

  override def recommend(userID: Long, howMany: Int): util.List[RecommendedItem] = {
    val rescorer = new GenderRescorer(men, women, usersRateMoreMen, usersRateLessMen, userID, model)
    delegate.recommend(userID, howMany, rescorer)
  }

  override def recommend(userID: Long, howMany: Int, rescorer: IDRescorer): util.List[RecommendedItem] = {
    delegate.recommend(userID, howMany, rescorer)
  }

  override def estimatePreference(userID: Long, itemID: Long): Float = {
    val rescorer = new GenderRescorer(men, women, usersRateMoreMen, usersRateLessMen, userID, model)
    rescorer.rescore(itemID, delegate.estimatePreference(userID, itemID)).toFloat
  }

  override def setPreference(userID: Long, itemID: Long, value: Float): Unit = {
    delegate.setPreference(userID, itemID, value)
  }

  override def removePreference(userID: Long, itemID: Long): Unit = {
    delegate.removePreference(userID, itemID)
  }

  override def getDataModel: DataModel = delegate.getDataModel

  override def refresh(alreadyRefreshed: util.Collection[Refreshable]): Unit = {
    delegate.refresh(alreadyRefreshed)
  }
}
