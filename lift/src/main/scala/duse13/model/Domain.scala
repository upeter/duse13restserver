package duse13.model

import org.joda.time._
import org.joda.time.DateTime

import duse13._
import scala.xml._
import serialize._
import util._
import net.liftweb.json.JsonDSL._
import scala.util.control.Exception

case class Person(val id: Option[Long], val name: String, val birthdate: DateTime, val hobbies: List[String])

object Person extends JSONSerializer[Person] with XMLSerializer[Person] {

  implicit val timeXmlFormat = new XmlFormat[Person] {

    override def fromXml(elem: Elem): Person = {
      require(elem != null, "xml must not be null!")
      val idAttr = (elem \\ "id")
      val id = if (!idAttr.text.isEmpty) Some(idAttr.text.toLong) else None
      val name = (elem \\ "name").text
      val birthdate = fmt.parseDateTime((elem \\ "birthdate").text)
      val hobbies = for (hobby <- (elem \\ "hobbies" \ "hobby")) yield hobby.text

      Person(id, name, birthdate, hobbies.toList)
    }

    override def toXml(person: Person): Elem = {
      <person>
        { person.id.map(id => <id>{ id }</id>).getOrElse(<id/>) }
        <name>{ person.name }</name>
        <birthdate>{ person.birthdate }</birthdate>
        <hobbies>
          { person.hobbies.map(hobby => { <hobby>{ hobby }</hobby> }) }
        </hobbies>
      </person>
    }
  }
  private def toInt(attr: Node): Option[Int] = Exception.catching(classOf[NumberFormatException]) opt { attr.text.toInt }

}