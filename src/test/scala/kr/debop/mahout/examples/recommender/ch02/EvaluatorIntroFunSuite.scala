package kr.debop.mahout.examples.recommender.ch02

import java.io.File

import kr.debop.mahout.examples.AbstractMahoutFunSuite
import org.apache.mahout.cf.taste.eval.RecommenderBuilder
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity
import org.apache.mahout.cf.taste.model.DataModel
import org.apache.mahout.cf.taste.recommender.Recommender
import org.apache.mahout.common.RandomUtils
import org.slf4j.LoggerFactory

/**
 * EvaluatorIntroFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class EvaluatorIntroFunSuite extends AbstractMahoutFunSuite {

  private val log = LoggerFactory.getLogger(getClass)

  test("Evaluator 소개") {
    RandomUtils.useTestSeed()

    val dataFile = new File("src/data/intro.csv")
    val model = new FileDataModel(dataFile)

    val evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator()
    val recommenderBuilder = new RecommenderBuilder {
      override def buildRecommender(dataModel: DataModel): Recommender = {
        val similarity = new PearsonCorrelationSimilarity(model)
        val neighborhood = new NearestNUserNeighborhood(2, similarity, model)

        new GenericUserBasedRecommender(model, neighborhood, similarity)
      }
    }
    // Use 70% of the data to train; test using the other 30%.
    val score = evaluator.evaluate(recommenderBuilder, null, model, 0.9, 1.0)
    log.debug(s"Score=$score")
  }

}
