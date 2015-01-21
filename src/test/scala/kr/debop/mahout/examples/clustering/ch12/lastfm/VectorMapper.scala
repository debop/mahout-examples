package kr.debop.mahout.examples.clustering.ch12.lastfm

import java.util
import java.util.regex.Pattern

import org.apache.hadoop.io.{DefaultStringifier, LongWritable, Text}
import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.util.GenericsUtil
import org.apache.mahout.math.{NamedVector, SequentialAccessSparseVector, VectorWritable}

/**
 * VectorMapper
 * @author sunghyouk.bae@gmail.com
 */
class VectorMapper extends Mapper[LongWritable, Text, Text, VectorWritable] {

  var splitter: Pattern = _
  var writer: VectorWritable = _
  var dictionary: util.Map[String, Integer] = _

  override def setup(context: Mapper[LongWritable, Text, Text, VectorWritable]#Context): Unit = {
    super.setup(context)

    val conf = context.getConfiguration
    val mapStringifier = new DefaultStringifier[util.Map[String, Integer]](conf, GenericsUtil.getClass(dictionary))

    dictionary = mapStringifier.fromString(conf.get("dictionary"))
    splitter = Pattern.compile("<sep>")
    writer = new VectorWritable()
  }

  override def map(key: LongWritable,
                   value: Text,
                   context: Mapper[LongWritable, Text, Text, VectorWritable]#Context): Unit = {

    //    Data Format:
    //      The data is formatted one entry per line as follows:
    //
    //    musicbrainz-artist-id<sep>artist-name<sep>tag-name<sep>raw-tag-count

    val fields = splitter.split(value.toString)
    if (fields.length < 4) {
      context.getCounter("Map", "LinesWithErrors").increment(1)
      return
    }

    val artist = fields(1)
    val tag = fields(2)
    val weight = fields(3).toDouble

    val vector = new NamedVector(new SequentialAccessSparseVector(dictionary.size), tag)
    vector.set(dictionary.get(artist), weight)
    writer.set(vector)
    context.write(new Text(tag), writer)
  }
}
