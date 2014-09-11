package com.ldaniels528.verify.support.zookeeper

import com.ldaniels528.verify.io.EndPoint
import org.apache.zookeeper.data.Stat

import scala.concurrent.Future
import scala.language.implicitConversions

/**
 * ZooKeeper Proxy
 * @author Lawrence Daniels <lawrence.daniels@gmail.com>
 */
trait ZKProxy {

  def close(): Unit

  def create(tuples: (String, Array[Byte])*): Iterable[String]

  def create(path: String, data: Array[Byte], ctx: Any): Future[Int]

  def ensurePath(path: String): ZKProxy

  def ensureParents(path: String): ZKProxy

  def delete(path: String): Unit

  def exists(path: String, watch: Boolean = false): Option[Stat]

  def getChildren(path: String, watch: Boolean = false): Seq[String]

  def getSessionId: Long

  def read(path: String): Option[Array[Byte]]

  def readDouble(path: String): Option[Double]

  def readInt(path: String): Option[Int]

  def readLong(path: String): Option[Long]

  def readString(path: String): Option[String]

  def reconnect(): Unit

  def update(path: String, data: Array[Byte]): Iterable[String]

  def updateLong(path: String, value: Long): Iterable[String]

}

/**
 * ZooKeeper Proxy Companion Object
 * @author Lawrence Daniels <lawrence.daniels@gmail.com>
 */
object ZKProxy {

  def apply(ep: EndPoint, callback: Option[ZkProxyCallBack] = None) = new ZKProxyV1(ep.host, ep.port, callback)

  /**
   * All implicit definitions are declared here
   * @author Lawrence Daniels <lawrence.daniels@gmail.com>
   */
  object Implicits {

    import java.nio.ByteBuffer

    implicit def byteBuffer2ByteArray(buf: ByteBuffer): Array[Byte] = {
      val bytes = new Array[Byte](buf.limit())
      buf.rewind()
      buf.get(bytes)
      bytes
    }

    implicit class ZKPathSplitter(path: String) {

      def splitNodes: List[String] = {
        val pcs = path.split("[/]").tail
        val list = pcs.foldLeft[List[String]](Nil) { (list, cur) =>
          val path = if (list.nonEmpty) s"${list.head}/$cur" else cur
          path :: list
        }
        list.reverse map (s => "/" + s)
      }

    }

  }

}