package kr.debop.mahout.examples.recommender.ch02

import java.io.File

import kr.debop.mahout.examples.AbstractMahoutFunSuite
import org.apache.mahout.cf.taste.eval.RecommenderBuilder
import org.apache.mahout.cf.taste.impl.eval.{AverageAbsoluteDifferenceRecommenderEvaluator, GenericRecommenderIRStatsEvaluator}
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood
import org.apache.mahout.cf.taste.impl.recommender.{GenericUserBasedRecommender, ItemAverageRecommender}
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity
import org.apache.mahout.cf.taste.model.DataModel
import org.apache.mahout.cf.taste.recommender.Recommender
import org.apache.mahout.common.RandomUtils
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

/**
 * EvaluatorByMovieLensFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class EvaluatorByMovieLensFunSuite extends AbstractMahoutFunSuite {

  private val log = LoggerFactory.getLogger(getClass)

  def dataFile = new File("src/data/ml-100k/ua.base")

  test("RecommendIntro") {

    val model = new FileDataModel(dataFile)

    val similarity = new PearsonCorrelationSimilarity(model)
    val neighborhood = new NearestNUserNeighborhood(2, similarity, model)

    val recommender = new GenericUserBasedRecommender(model, neighborhood, similarity)

    val recommendations = recommender.recommend(1, 2)

    recommendations.asScala.foreach { recommendation =>
      println(recommendation)
    }
  }

  test("추천기 평가하기. 실제와 예측값의 차이로 평가") {
    RandomUtils.useTestSeed()

    val model = new FileDataModel(dataFile)

    val evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator()
    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        val similarity = new PearsonCorrelationSimilarity(dataModel)
        val neighborhood = new NearestNUserNeighborhood(2, similarity, dataModel)

        new GenericUserBasedRecommender(dataModel, neighborhood, similarity)
      }
    }
    // Use 70% of the data to train; test using the other 30%.
    val score = evaluator.evaluate(recommenderBuilder, null, model, 0.7, 0.3)
    println(s"Score=$score")
  }

  test("추천기 정확율/재현율") {
    RandomUtils.useTestSeed()

    val model = new FileDataModel(dataFile)

    val evaluator = new GenericRecommenderIRStatsEvaluator()

    // Build the same recommender for testing that we did last time:
    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        val similarity = new PearsonCorrelationSimilarity(dataModel)
        val neighborhood = new NearestNUserNeighborhood(2, similarity, dataModel)

        new GenericUserBasedRecommender(dataModel, neighborhood, similarity)
      }
    }

    // Evaluate precision and recall "at 2":
    val stats = evaluator.evaluate(recommenderBuilder,
                                    null,
                                    model,
                                    null,
                                    2,
                                    GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,
                                    1.0)

    println(s"정확율(precision)=${stats.getPrecision}")
    println(s"재현율(recall)=${stats.getRecall}")
    println(s"stats=$stats")
  }

  test("SlopeOneRecommender 테스트") {
    RandomUtils.useTestSeed()

    val model = new FileDataModel(dataFile)

    val evaluator = new GenericRecommenderIRStatsEvaluator()

    // Build the same recommender for testing that we did last time:
    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        new ItemAverageRecommender(dataModel)
      }
    }

    // Evaluate precision and recall "at 2":
    val stats = evaluator.evaluate(recommenderBuilder,
                                    null,
                                    model,
                                    null,
                                    2,
                                    GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,
                                    1.0)

    println(s"정확율(precision)=${stats.getPrecision}")
    println(s"재현율(recall)=${stats.getRecall}")
    println(s"stats=$stats")
  }
}
