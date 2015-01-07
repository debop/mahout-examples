package kr.debop.mahout.examples.recommender.ch05

import java.io.{File, IOException}

import kr.debop.mahout.examples.AbstractMahoutFunSuite
import org.apache.mahout.cf.taste.common.TasteException
import org.apache.mahout.cf.taste.eval.RecommenderBuilder
import org.apache.mahout.cf.taste.impl.eval.{AverageAbsoluteDifferenceRecommenderEvaluator, GenericRecommenderIRStatsEvaluator, LoadEvaluator}
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel
import org.apache.mahout.cf.taste.impl.model.{GenericBooleanPrefDataModel, GenericUserPreferenceArray, PlusAnonymousUserDataModel}
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity
import org.apache.mahout.cf.taste.model.DataModel
import org.apache.mahout.cf.taste.recommender.Recommender
import org.slf4j.LoggerFactory

/**
 * LibimsetiEvalRunner
 * @author sunghyouk.bae@gmail.com
 */
class LibimsetiEvalRunnerFunSuite extends AbstractMahoutFunSuite {

  private val log = LoggerFactory.getLogger(getClass)

  def ratingsFile: File = new File("src/data/libimseti/ratings.dat")

  test("LibimsetiEvalRunner") {
    val model = new FileDataModel(ratingsFile)
    val evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator()

    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        try {
          new LibimsetiRecommender(dataModel)
        } catch {
          case e: IOException => throw new TasteException(e)
        }
      }
    }

    val score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.1)
    println(s"LibimsetiEvalRunner score=$score")
  }

  test("LibimsetiIREvalRunner") {
    val fdModel = new FileDataModel(ratingsFile)
    val model = new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(fdModel))
    val evaluator = new GenericRecommenderIRStatsEvaluator()

    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        val similarity = new TanimotoCoefficientSimilarity(dataModel)
        val neighborhood = new NearestNUserNeighborhood(2, similarity, dataModel)
        new GenericBooleanPrefUserBasedRecommender(dataModel, neighborhood, similarity)
      }
    }

    val stats = evaluator.evaluate(recommenderBuilder, null, model, null, 10, Double.NaN, 0.1)
    println(s"IREvalRunner Stats=$stats")
  }

  test("LoadRunner") {
    val model = new FileDataModel(ratingsFile)
    val recommender = new LibimsetiRecommender(model)
    LoadEvaluator.runLoad(recommender)
  }

  test("Anonymous Recommender") {
    val anonymousPrefs = new GenericUserPreferenceArray(3)
    anonymousPrefs.setUserID(0, PlusAnonymousUserDataModel.TEMP_USER_ID)

    anonymousPrefs.setItemID(0, 123L)
    anonymousPrefs.setValue(0, 1.0f)
    anonymousPrefs.setItemID(1, 123L)
    anonymousPrefs.setValue(1, 3.0f)
    anonymousPrefs.setItemID(2, 123L)
    anonymousPrefs.setValue(2, 2.0f)

    val recommender = new LibimsetiWithAnonymousRecommender()
    val recommendations = recommender.recommend(anonymousPrefs, 10)

    println(s"Recommendations=$recommendations")
  }

}
