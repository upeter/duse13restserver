package duse13.serialize

import org.joda.time._
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import org.joda.time.format._
import org.joda.time.DateTime
import net.liftweb.json.JsonAST.{ JValue, JArray }
import net.liftweb.json.ext.JodaTimeSerializers
import io.Source
import scala.xml._
package object util {

  val fmt = ISODateTimeFormat.dateTime()

  implicit val formats: Formats = Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all
  def deserialize[T](json: String)(implicit m: Manifest[T]): T = {
    Serialization.read[T](json)
  }
  def serializeToJson(s: String): JValue = {
    parse(serializeToJsonStr(s))
  }
  def serializeToJsonStr(t: AnyRef): String = {
    Serialization.write(t)
  }

  def fileFromClasspathAsString(fileName: String) = {
    Source.fromInputStream(fileFromClasspathAsInputStream(fileName)).mkString
  }

  def fileFromClasspathAsInputStream(fileName: String) = {
    getClass.getClassLoader.getResourceAsStream(fileName)
  }
  object AsDateTime {
    def unapply(isoDateString: String): Option[DateTime] = {
      try { Some(fmt.parseDateTime(isoDateString)) }
      catch {
        case e: Exception => None
      }

    }
  }

}
