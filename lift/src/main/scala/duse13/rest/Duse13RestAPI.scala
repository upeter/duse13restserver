package duse13.rest
import net.liftweb.http.rest.RestHelper
import net.liftweb.common._
import duse13.model.Person;
//import net.liftweb.json.JsonAST._
import org.joda.time._
import net.liftweb._
import json._
import net.liftweb.http._
import scala.xml._
import net.liftweb.util.BasicTypesHelpers._

object Duse13RestAPI extends RestHelper with Logger {

  serve {
    // /search.xml?q=name
    case Req("search" :: _, "xml", GetRequest) => {
      val xml: Elem = PersonRepo.findByName(S.param("q").get).map(_.toXml).getOrElse(<notfound/>)
      xml
    }

    // /search.json?q=name
    case Req("search" :: _, "json", GetRequest) => {
      val json: JValue = PersonRepo.findByName(S.param("q").get).map(p => JsonParser.parse(p.toJson)).getOrElse(JString("notfound"))
      json
    }

    // /update.xml
    case req @ Req("update" :: _, "xml", PutRequest) => {
      info("update xml " + new String(req.body.get))
      req.body.toOption.map {
        body =>
          PersonRepo.update(Person.fromXml(XML.loadString(new String(body))))
      }
      Full(OkResponse())
    }
    // /update.xml
    case req @ Req("update" :: _, "json", PutRequest) => {
      info("update json " + new String(req.body.get))
      req.body.toOption.map {
        body =>
          PersonRepo.update(Person.fromJson(new String(body)))
      }
      Full(OkResponse())
    }
    // /add.xml
    case req @ Req("add" :: _, "xml", PostRequest) => {
      info("add xml " + new String(req.body.get))
      val xml: Elem = PersonRepo.add(Person.fromXml(XML.loadString(new String(req.body.get)))).toXml
      xml

    }
    // /add.xml
    case req @ Req("add" :: _, "json", PostRequest) => {
      info("add json " + new String(req.body.get))
      val json = PersonRepo.add(Person.fromJson(new String(req.body.get))).toJson
      JsonParser.parse(json)
    }
    // /delete
    case req @ Req("delete" :: AsLong(id) :: _, _, DeleteRequest) => {
      info("delete " + id)
      PersonRepo.deleteById(id)
      Full(OkResponse())
    }

  }

  object PersonRepo {
    var persons = List(
      Person(Some(1), "John Doe", new DateTime, List("swimming")),
      Person(Some(2), "Jack Danniels", new DateTime, List("drinking", "dancing")),
      Person(Some(3), "Martin Odersky", new DateTime, List("programming")))

    def findByName(name: String): Option[Person] = persons.find(_.name.toLowerCase.contains(name.toLowerCase))
    def findById(id: Long): Option[Person] = persons.find(_.id == id)
    def deleteById(id: Long): Boolean = {
      val (todel, rest) = persons.partition(_.id.get == id)
      persons = rest
      !todel.isEmpty
    }
    def add(p: Person): Person = {
      val nextId: Long = persons.map(_.id.get).max + 1
      val person = p.copy(id = Some(nextId))
      persons = persons :+ person
      person
    }
    def update(p: Person) = {
      deleteById(p.id.get)
      persons :+= p
      info(persons)
    }

  }

}