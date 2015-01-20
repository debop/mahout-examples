package kr.debop.mahout.examples.clustering.ch10

import java.io.Reader

import org.apache.lucene.analysis.Analyzer.TokenStreamComponents
import org.apache.lucene.analysis.core.{LowerCaseFilter, StopFilter}
import org.apache.lucene.analysis.en.PorterStemFilter
import org.apache.lucene.analysis.miscellaneous.LengthFilter
import org.apache.lucene.analysis.standard.{StandardAnalyzer, StandardFilter, StandardTokenizer}
import org.apache.lucene.analysis.{Analyzer, TokenStream}
import org.apache.lucene.util.Version

/**
 * MyAnalyzer
 * @author sunghyouk.bae@gmail.com
 */
class MyAnalyzer extends Analyzer {

  override def createComponents(fieldName: String, reader: Reader): TokenStreamComponents = {
    val source = new StandardTokenizer(Version.LUCENE_46, reader)
    val standardFilter = new StandardFilter(Version.LUCENE_46, source)
    val lowercaseFilter = new LowerCaseFilter(Version.LUCENE_46, standardFilter)
    var filter: TokenStream = new LengthFilter(Version.LUCENE_46, true, lowercaseFilter, 3, 50)
    filter = new StopFilter(Version.LUCENE_46, filter, StandardAnalyzer.STOP_WORDS_SET)
    filter = new PorterStemFilter(filter)

    new TokenStreamComponents(source, filter)
  }
}
