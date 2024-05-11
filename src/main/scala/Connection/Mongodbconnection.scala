package Connection
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._



object Mongodbcollection {
  private val mongoClient = MongoClient("mongodb://root:root@mongodb:27017")
  val database: MongoDatabase = mongoClient.getDatabase("UniverPractice")
  val practiceCollection: MongoCollection[Document] = database.getCollection("practice")
  val directorCollection: MongoCollection[Document] = database.getCollection("directer")
  val zadachiCollection: MongoCollection[Document] = database.getCollection("zadachi")
  val mestoprovedenyaCollection: MongoCollection[Document] = database.getCollection("mestoprovedenya")

}