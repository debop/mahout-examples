package kr.debop.mahout.examples.clustering.ch09

import java.io.Reader

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents
import org.apache.lucene.analysis.core.{LowerCaseFilter, StopFilter}
import org.apache.lucene.analysis.standard.{StandardAnalyzer, StandardFilter, StandardTokenizer}
import org.apache.lucene.util.Version

/**
 * MyAnalyzer
 * @author sunghyouk.bae@gmail.com
 */
class MyAnalyzer extends Analyzer {

  override def createComponents(fieldName: String, reader: Reader): TokenStreamComponents = {
    val source = new StandardTokenizer(Version.LUCENE_CURRENT, reader)
    val standardFilter = new StandardFilter(Version.LUCENE_CURRENT, source)
    val lowercaseFilter = new LowerCaseFilter(Version.LUCENE_CURRENT, standardFilter)
    val filter = new StopFilter(Version.LUCENE_CURRENT, lowercaseFilter, StandardAnalyzer.STOP_WORDS_SET)

    new TokenStreamComponents(source, filter)
  }
}
