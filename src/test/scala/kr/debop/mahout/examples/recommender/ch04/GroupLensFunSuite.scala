package kr.debop.mahout.examples.recommender.ch04

import java.io.File

import kr.debop.mahout.examples.AbstractMahoutFunSuite
import org.apache.mahout.cf.taste.eval.RecommenderBuilder
import org.apache.mahout.cf.taste.impl.eval.{AverageAbsoluteDifferenceRecommenderEvaluator, LoadEvaluator}
import org.apache.mahout.cf.taste.impl.neighborhood.{NearestNUserNeighborhood, ThresholdUserNeighborhood}
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity
import org.apache.mahout.cf.taste.model.DataModel
import org.apache.mahout.cf.taste.recommender.Recommender
import org.apache.mahout.cf.taste.similarity.precompute.example.GroupLensDataModel

/**
 * GroupLensFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class GroupLensFunSuite extends AbstractMahoutFunSuite {

  def ratingsFile: File = new File("src/data/ml-1m/ratings.dat")

  test("GroupLens DataModel") {
    val model = new GroupLensDataModel(ratingsFile)
    val similarity = new PearsonCorrelationSimilarity(model)
    val neighborhood = new NearestNUserNeighborhood(100, similarity, model)
    val recommender = new GenericUserBasedRecommender(model, neighborhood, similarity)

    LoadEvaluator.runLoad(recommender)
  }

  test("4.2.5 고정 크기 이웃 - 100") {
    val model = new GroupLensDataModel(ratingsFile)
    val evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator()
    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        val similarity = new PearsonCorrelationSimilarity(dataModel)
        val neighborhood = new NearestNUserNeighborhood(100, similarity, dataModel)
        new GenericUserBasedRecommender(dataModel, neighborhood, similarity)
      }
    }

    val score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.05)
    println(s"Score=$score")
  }

  test("4.2.5 고정 크기 이웃 - 10") {
    val model = new GroupLensDataModel(ratingsFile)
    val evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator()
    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        val similarity = new PearsonCorrelationSimilarity(dataModel)
        val neighborhood = new NearestNUserNeighborhood(10, similarity, dataModel)
        new GenericUserBasedRecommender(dataModel, neighborhood, similarity)
      }
    }

    val score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.05)
    println(s"Score=$score")
  }

  test("4.2.5 고정 크기 이웃 - 500") {
    val model = new GroupLensDataModel(ratingsFile)
    val evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator()
    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        val similarity = new PearsonCorrelationSimilarity(dataModel)
        val neighborhood = new NearestNUserNeighborhood(500, similarity, dataModel)
        new GenericUserBasedRecommender(dataModel, neighborhood, similarity)
      }
    }

    val score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.05)
    println(s"Score=$score")
  }

  test("4.2.6 임계치 기반 이웃 - 0.7") {
    val model = new GroupLensDataModel(ratingsFile)
    val evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator()
    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        val similarity = new PearsonCorrelationSimilarity(dataModel)
        val neighborhood = new ThresholdUserNeighborhood(0.7, similarity, dataModel)
        new GenericUserBasedRecommender(dataModel, neighborhood, similarity)
      }
    }

    val score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.05)
    println(s"Score=$score")
  }

  test("4.2.6 임계치 기반 이웃 - 0.5") {
    val model = new GroupLensDataModel(ratingsFile)
    val evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator()
    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        val similarity = new PearsonCorrelationSimilarity(dataModel)
        val neighborhood = new ThresholdUserNeighborhood(0.5, similarity, dataModel)
        new GenericUserBasedRecommender(dataModel, neighborhood, similarity)
      }
    }

    val score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.05)
    println(s"Score=$score")
  }
}
