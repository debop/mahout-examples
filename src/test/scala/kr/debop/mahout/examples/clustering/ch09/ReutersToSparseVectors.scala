package kr.debop.mahout.examples.clustering.ch09

import java.io.File

import kr.debop.mahout.examples.AbstractMahoutFunSuite
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.{SequenceFile, Text}
import org.apache.lucene.analysis.Analyzer
import org.apache.mahout.common.HadoopUtil
import org.apache.mahout.math.VectorWritable
import org.apache.mahout.vectorizer.tfidf.TFIDFConverter
import org.apache.mahout.vectorizer.{DictionaryVectorizer, DocumentProcessor}

/**
 * ReutersToSparseVectors
 * @author sunghyouk.bae@gmail.com
 */
class ReutersToSparseVectors extends AbstractMahoutFunSuite {

  test("Reuters to SparseVectors") {

    val minSupport = 5
    val minDf = 5
    val maxDFPercent = 95
    val maxNGramSize = 1
    val minLLRValue = 50: Float
    val reduceTasks = 1
    val chunkSize = 200
    val norm = 2
    val sequentialAccessOutput = true

    val inputDir = "inputDir"
    val inputFile = new File(inputDir)
    if (!inputFile.exists()) {
      inputFile.mkdir()
    }

    val conf = new Configuration()
    val fs = FileSystem.get(conf)

    val outputDir = "reuters"
    HadoopUtil.delete(conf, new Path(outputDir))
    val tokenizedPath = new Path(outputDir, DocumentProcessor.TOKENIZED_DOCUMENT_OUTPUT_FOLDER)
    val analyzer = new MyAnalyzer
    DocumentProcessor.tokenizeDocuments(new Path(inputDir),
                                         analyzer.getClass.asSubclass(classOf[Analyzer]),
                                         tokenizedPath,
                                         conf)

    DictionaryVectorizer.createTermFrequencyVectors(tokenizedPath,
                                                     new Path(outputDir),
                                                     DictionaryVectorizer.DOCUMENT_VECTOR_OUTPUT_FOLDER,
                                                     conf,
                                                     minSupport,
                                                     maxNGramSize,
                                                     minLLRValue,
                                                     2,
                                                     true,
                                                     reduceTasks,
                                                     chunkSize,
                                                     sequentialAccessOutput,
                                                     false)

    val dfData = TFIDFConverter.calculateDF(new Path(outputDir, DictionaryVectorizer.DOCUMENT_VECTOR_OUTPUT_FOLDER),
                                             new Path(outputDir),
                                             conf,
                                             chunkSize)
    TFIDFConverter.processTfIdf(new Path(outputDir, DictionaryVectorizer.DOCUMENT_VECTOR_OUTPUT_FOLDER),
                                 new Path(outputDir),
                                 conf,
                                 dfData,
                                 minDf,
                                 maxDFPercent,
                                 norm,
                                 true,
                                 sequentialAccessOutput,
                                 false,
                                 reduceTasks)

    val vectorsFolder = outputDir + "/tfidf-vectors"

    val reader = new SequenceFile.Reader(fs, new Path(vectorsFolder, "part-r-00000"), conf)
    try {
      val key = new Text()
      val value = new VectorWritable()

      while (reader.next(key, value)) {
        println(s"$key => ${value.get.asFormatString()}")
      }
    } finally {
      reader.close()
    }
  }

}
