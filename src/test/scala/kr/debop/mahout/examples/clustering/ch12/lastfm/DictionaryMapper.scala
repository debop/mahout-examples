package kr.debop.mahout.examples.clustering.ch12.lastfm

import java.util.regex.Pattern

import org.apache.hadoop.io.{IntWritable, LongWritable, Text}
import org.apache.hadoop.mapreduce.Mapper

/**
 * DictionaryMapper
 * @author sunghyouk.bae@gmail.com
 */
class DictionaryMapper extends Mapper[LongWritable, Text, Text, IntWritable] {

  var splitter: Pattern = _

  override def setup(context: Mapper[LongWritable, Text, Text, IntWritable]#Context): Unit = {
    super.setup(context)
    splitter = Pattern.compile("<sep>")
  }
  override def map(key: LongWritable,
                   line: Text,
                   context: Mapper[LongWritable, Text, Text, IntWritable]#Context): Unit = {
    //    Data Format:
    //      The data is formatted one entry per line as follows:
    //
    //    musicbrainz-artist-id<sep>artist-name<sep>tag-name<sep>raw-tag-count

    val fields = splitter.split(line.toString)
    if (fields.length < 4) {
      context.getCounter("Map", "LinesWithErrors").increment(1)
    } else {
      val artist = fields(1)   // artist 추출
      context.write(new Text(artist), new IntWritable(0))   // artist 를 key로 내보낸다.
    }
  }
}
