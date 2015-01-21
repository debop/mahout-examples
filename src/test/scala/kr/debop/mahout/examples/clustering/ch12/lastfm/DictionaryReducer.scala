package kr.debop.mahout.examples.clustering.ch12.lastfm

import java.lang.Iterable

import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Reducer

/**
 * DictionaryReducer
 * @author sunghyouk.bae@gmail.com
 */
class DictionaryReducer extends Reducer[Text, IntWritable, Text, IntWritable] {

  override def reduce(artist: Text,
                      values: Iterable[IntWritable],
                      context: Reducer[Text, IntWritable, Text, IntWritable]#Context): Unit = {
    context.write(artist, new IntWritable(0))
  }
}
