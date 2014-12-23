package kr.debop.mahout.examples.recommender.ch04

import java.io.File

import kr.debop.mahout.examples.AbstractMahoutFunSuite
import org.apache.mahout.cf.taste.eval.RecommenderBuilder
import org.apache.mahout.cf.taste.impl.eval.{AverageAbsoluteDifferenceRecommenderEvaluator, LoadEvaluator}
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood
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

  test("GroupLens 10M Eval") {
    val model = new GroupLensDataModel(ratingsFile)
    val evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator()
    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        val similarity = new PearsonCorrelationSimilarity(dataModel)
        val neighborhood = new NearestNUserNeighborhood(100, similarity, model)
        new GenericUserBasedRecommender(model, neighborhood, similarity)
      }
    }

    val score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.05)
    println(s"Score=$score")
  }

}
