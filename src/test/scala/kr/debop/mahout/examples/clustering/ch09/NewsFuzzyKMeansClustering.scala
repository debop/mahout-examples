package kr.debop.mahout.examples.clustering.ch09

import java.io.File

import kr.debop.mahout.examples.AbstractMahoutFunSuite
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{Path, FileSystem}
import org.apache.hadoop.io.{IntWritable, SequenceFile}
import org.apache.lucene.analysis.Analyzer
import org.apache.mahout.clustering.Cluster
import org.apache.mahout.clustering.canopy.CanopyDriver
import org.apache.mahout.clustering.classify.WeightedVectorWritable
import org.apache.mahout.clustering.fuzzykmeans.FuzzyKMeansDriver
import org.apache.mahout.common.HadoopUtil
import org.apache.mahout.common.distance.ManhattanDistanceMeasure
import org.apache.mahout.vectorizer.tfidf.TFIDFConverter
import org.apache.mahout.vectorizer.{DictionaryVectorizer, DocumentProcessor}

/**
 * NewsFuzzyKMeansClustering
 * @author sunghyouk.bae@gmail.com
 */
class NewsFuzzyKMeansClustering extends AbstractMahoutFunSuite {

  test("News Fuzzy K-Means Clustering") {

    val minSupport = 5
    val minDf = 10
    val maxDFPercent = 70
    val maxNGramSize = 1
    val minLLRValue = 200
    val reduceTasks = 1
    val chunkSize = 200
    val norm = 2
    val sequentialAccessOutput = true

    val inputDir = "inputDir"

    val input = new File(inputDir)
    if (!input.exists()) {
      input.mkdir
    }

    val conf = new Configuration()
    val fs = FileSystem.get(conf)

    val outputDir = "newsClusters"
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
    val canopyCentroids = outputDir + "/canopy-centroids"
    val clusterOutput = outputDir + "/clusters/"

    CanopyDriver.run(conf,
                      new Path(vectorsFolder),
                      new Path(canopyCentroids),
                      new ManhattanDistanceMeasure,
                      3000.0,
                      2000.0,
                      false,
                      0.0,
                      false)

    FuzzyKMeansDriver.run(conf,
                           new Path(vectorsFolder),
                           new Path(canopyCentroids, Cluster.INITIAL_CLUSTERS_DIR + Cluster.FINAL_ITERATION_SUFFIX),
                           new Path(clusterOutput),
                           0.01,
                           20,
                           2.0f,
                           true,
                           true,
                           0.0,
                           false)

    val reader = new SequenceFile.Reader(fs, new Path(clusterOutput + Cluster.CLUSTERED_POINTS_DIR + "/part-m-00000"), conf)

    val key = new IntWritable
    val value = new WeightedVectorWritable
    while (reader.next(key, value)) {
      println(s"Cluster: $key ${value.getVector.asFormatString()}")
    }
    reader.close()
    analyzer.close()
  }

}