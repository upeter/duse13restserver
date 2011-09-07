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
import util._


trait JSONSerializer[T <: AnyRef] {

  implicit def pimpWithToJson(p: T) = new {
    def toJson = serializeToJsonStr(p)
  }
  def fromJson(json: String)(implicit m: Manifest[T]) = deserialize[T](json)

}

trait XMLSerializer[T] {
   implicit def pimpWithToXML(p: T) = new {
    def toXml(implicit f:XmlFormat[T]):Elem = f.toXml(p)
  }
  def fromXml(xml:Elem)(implicit f:XmlFormat[T]) = f.fromXml(xml)
 
  
}

trait XmlFormat[A] {

  def fromXml(xml: Elem): A

  def toXml(a: A): Elem
}