package kr.debop.mahout.examples.recommender.ch05

import org.apache.mahout.cf.taste.recommender.IDRescorer

/**
 * GenreRescorer
 * @author sunghyouk.bae@gmail.com
 */
class GenreRescorer(val currentGenre: Genre) extends IDRescorer {

  override def rescore(itemId: Long, originalScore: Double): Double = {
    val book = BookManager.lookupBook(itemId)

    if (book != null && book.genre.equals(currentGenre)) originalScore * 1.2
    else originalScore
  }

  override def isFiltered(itemId: Long): Boolean = {
    val book = BookManager.lookupBook(itemId)

    if (book == null) true
    else book.isOutOfStock   // 재고가 없는 책 필터링하기
  }
}
