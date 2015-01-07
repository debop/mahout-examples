package kr.debop.mahout.examples.recommender.ch05

import java.io.File

import org.apache.mahout.cf.taste.impl.common.FastIDSet
import org.apache.mahout.cf.taste.model.DataModel
import org.apache.mahout.cf.taste.recommender.IDRescorer
import org.apache.mahout.common.iterator.FileLineIterable
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

/**
 * GenderRescorer
 * @author sunghyouk.bae@gmail.com
 */
class GenderRescorer(val men: FastIDSet,
                     val women: FastIDSet,
                     val usersRateMoreMen: FastIDSet,
                     val usersRateLessMen: FastIDSet,
                     userId: Long,
                     model: DataModel) extends IDRescorer {
  private val log = LoggerFactory.getLogger(getClass)

  val filterMen = ratesMoreMen(userId, model)

  override def rescore(id: Long, originalScore: Double): Double = {
    if (isFiltered(id)) Double.NaN
    else originalScore
  }

  override def isFiltered(profileId: Long): Boolean = {
    if (filterMen) men.contains(profileId)
    else women.contains(profileId)
  }

  def ratesMoreMen(userId: Long, model: DataModel): Boolean = {
    if (usersRateMoreMen.contains(userId)) return true
    if (usersRateLessMen.contains(userId)) return false

    val prefs = model.getPreferencesFromUser(userId)
    var menCount = 0
    var womenCount = 0
    prefs.asScala.foreach { pref =>
      val profileId = pref.getItemID
      if (men.contains(profileId)) menCount += 1
      else if (women.contains(profileId)) womenCount += 1
    }
    // 남자에 평점을 내린 사용자는 남성 프로파일을 좋아할 것이다.
    val isRatesMoreMen = menCount > womenCount
    if (isRatesMoreMen) usersRateMoreMen.add(userId)
    else usersRateLessMen.add(userId)

    isRatesMoreMen
  }
}

object GenderRescorer {

  private val log = LoggerFactory.getLogger(getClass)

  def parseMenWomen(genderFile: File): Array[FastIDSet] = {
    val men = new FastIDSet(50000)
    val women = new FastIDSet(50000)

    new FileLineIterable(genderFile).asScala.foreach { line =>
      val comma = line.indexOf(',')
      val gender = line.charAt(comma + 1)

      // Unknown, Man, Female
      if (gender != 'U') {
        val profileId = line.substring(0, comma).toLong
        log.trace(s"profileId=$profileId")

        if (gender == 'M') men.add(profileId)
        else women.add(profileId)
      }
    }
    // 내부 메모리를 정리하여, 접근 속도를 높힙니다.
    men.rehash()
    women.rehash()
    Array[FastIDSet](men, women)
  }
}
