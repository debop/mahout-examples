package kr.debop.mahout.examples.clustering.ch12

import kr.debop.mahout.examples.AbstractMahoutFunSuite
import kr.debop.mahout.examples.clustering.ch12.lastfm.VectorCreationJob
import org.apache.hadoop.fs.Path

/**
 * CreateLastfmDataset
 * @author sunghyouk.bae@gmail.com
 */
class CreateLastfmDataset extends AbstractMahoutFunSuite {

  test("Create Last.fm Dataset") {
    val inputDir = new Path("src/data/Lastfm-ArtistTags2007/ArtistTags.dat")
    val dictionaryDir = new Path("lastfm_dict")
    val outputDir = new Path("lastfm_vectors")

    VectorCreationJob.generateDictionary(inputDir, dictionaryDir)
    VectorCreationJob.createVectors(inputDir, outputDir, dictionaryDir)
  }

}
