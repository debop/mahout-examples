package kr.debop.mahout.examples.recommender.ch05

import java.io.File
import java.util

import org.apache.mahout.cf.taste.impl.model.PlusAnonymousUserDataModel
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel
import org.apache.mahout.cf.taste.model.{PreferenceArray, DataModel}
import org.apache.mahout.cf.taste.recommender.RecommendedItem

/**
 * LibimsetiWithAnonymousRecommender
 * @author sunghyouk.bae@gmail.com
 */
class LibimsetiWithAnonymousRecommender(private val _model:DataModel)
  extends LibimsetiRecommender(new PlusAnonymousUserDataModel(_model)) {

  def this() = this(new FileDataModel(new File("src/data/libimseti/ratings.dat")))

  val plusAnonymousModel = getDataModel().asInstanceOf[PlusAnonymousUserDataModel]

  def recommend(anonymousUserPrefs: PreferenceArray,
                 howMany:Int): util.List[RecommendedItem] = synchronized {
    plusAnonymousModel.setTempPrefs(anonymousUserPrefs)
    val recommendations = recommend(PlusAnonymousUserDataModel.TEMP_USER_ID, howMany, null)
    plusAnonymousModel.clearTempPrefs()
    recommendations
  }
}
