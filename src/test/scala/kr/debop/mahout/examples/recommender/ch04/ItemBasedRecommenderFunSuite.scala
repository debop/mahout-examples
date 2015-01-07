package kr.debop.mahout.examples.recommender.ch04

import java.io.File

import kr.debop.mahout.examples.AbstractMahoutFunSuite
import org.apache.mahout.cf.taste.eval.RecommenderBuilder
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender
import org.apache.mahout.cf.taste.impl.similarity._
import org.apache.mahout.cf.taste.model.DataModel
import org.apache.mahout.cf.taste.recommender.Recommender
import org.apache.mahout.cf.taste.similarity.precompute.example.GroupLensDataModel

/**
 * 4.4 Item 기반 추천
 * @author sunghyouk.bae@gmail.com
 */
class ItemBasedRecommenderFunSuite extends AbstractMahoutFunSuite {

  def ratingsFile: File = new File("src/data/ml-1m/ratings.dat")

  test("4.4.2 아이템 기반 추천기") {
    val model = new GroupLensDataModel(ratingsFile)
    val evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator()
    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        val similarity = new PearsonCorrelationSimilarity(dataModel)
        new GenericItemBasedRecommender(dataModel, similarity)
      }
    }

    val score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.05)
    println(s"Score=$score")
  }

  test("4.3.4 유클리드 거리 기반의 유사도 정의") {
    val model = new GroupLensDataModel(ratingsFile)
    val evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator()
    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        val similarity = new EuclideanDistanceSimilarity(dataModel)
        new GenericItemBasedRecommender(dataModel, similarity)
      }
    }

    val score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.05)
    println(s"Score=$score")
  }

  test("4.3.7 Tanimoto Coefficient Similarity") {
    val model = new GroupLensDataModel(ratingsFile)
    val evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator()
    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        val similarity = new TanimotoCoefficientSimilarity(dataModel)
        new GenericItemBasedRecommender(dataModel, similarity)
      }
    }

    val score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.05)
    println(s"Score=$score")
  }

  test("4.3.8 LogLikelihoodSimilarity Test") {
    val model = new GroupLensDataModel(ratingsFile)
    val evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator()
    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        val similarity = new LogLikelihoodSimilarity(dataModel)
        new GenericItemBasedRecommender(dataModel, similarity)
      }
    }

    val score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.05)
    println(s"Score=$score")
  }

  test("4.3.9 선호 추정하기") {
    val model = new GroupLensDataModel(ratingsFile)
    val evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator()
    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        val similarity = new PearsonCorrelationSimilarity(dataModel)
        similarity.setPreferenceInferrer(new AveragingPreferenceInferrer(dataModel))
        new GenericItemBasedRecommender(dataModel, similarity)
      }
    }

    val score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.05)
    println(s"Score=$score")
  }



}
