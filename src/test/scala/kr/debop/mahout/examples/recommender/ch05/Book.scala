package kr.debop.mahout.examples.recommender.ch05

/**
 * Book
 * @author sunghyouk.bae@gmail.com
 */
trait Book {

  def genre: Genre

  def isOutOfStock:Boolean

}
