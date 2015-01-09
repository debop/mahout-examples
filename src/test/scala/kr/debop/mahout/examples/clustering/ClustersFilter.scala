package kr.debop.mahout.examples.clustering

import org.apache.hadoop.fs.{Path, PathFilter}

/**
 * ClustersFilter
 * @author sunghyouk.bae@gmail.com
 */
class ClustersFilter extends PathFilter {

  override def accept(path: Path): Boolean = {
    val pathString = path.toString
    pathString.contains("/clusters-")
  }
}
