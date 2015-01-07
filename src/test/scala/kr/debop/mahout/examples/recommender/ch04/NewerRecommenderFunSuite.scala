package kr.debop.mahout.examples.recommender.ch04

import java.io.File

import kr.debop.mahout.examples.AbstractMahoutFunSuite
import org.apache.mahout.cf.taste.eval.RecommenderBuilder
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator
import org.apache.mahout.cf.taste.impl.recommender.svd.{ALSWRFactorizer, SVDRecommender}
import org.apache.mahout.cf.taste.model.DataModel
import org.apache.mahout.cf.taste.recommender.Recommender
import org.apache.mahout.cf.taste.similarity.precompute.example.GroupLensDataModel

/**
 * NewerRecommenderFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class NewerRecommenderFunSuite extends AbstractMahoutFunSuite {

  def ratingsFile: File = new File("src/data/ml-1m/ratings.dat")


  test("4.6.1 Single Value Decomposition") {
    val model = new GroupLensDataModel(ratingsFile)
    val evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator()
    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        new SVDRecommender(dataModel, new ALSWRFactorizer(dataModel, 10, 0.05, 10))
      }
    }

    val score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.05)
    println(s"Score=$score")
  }

  test("4.6.1 Single Value Decomposition - 100명") {
    val model = new GroupLensDataModel(ratingsFile)
    val evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator()
    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        new SVDRecommender(dataModel, new ALSWRFactorizer(dataModel, 100, 0.05, 10))
      }
    }

    val score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.05)
    println(s"Score=$score")
  }

//  test("4.6.3 군집 기반 추천") {
//    val model = new GroupLensDataModel(ratingsFile)
//    val evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator()
//    val recommenderBuilder = new RecommenderBuilder {
//      override def buildRecommender(dataModel: DataModel): Recommender = {
//        val similarity = new LogLikelihoodSimilarity(dataModel)
//        new TreeClusteringRecommender()
//      }
//    }
//
//    val score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.05)
//    println(s"Score=$score")
//  }
}
