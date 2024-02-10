package Repository

import org.mongodb.scala.Document
import org.mongodb.scala.bson.{BsonDocument, BsonInt32, BsonString}
import org.mongodb.scala.model.Filters.equal
import scala.concurrent.{ExecutionContext, Future}
import Connection._
import Model._

class DirectorRepository(implicit ec: ExecutionContext) {

  def getAllDirectors(): Future[List[Director]] = {
    val futureDirectors = Mongodbcollection.directorCollection.find().toFuture()

    futureDirectors.map { docs =>
      Option(docs).map(_.map { doc =>
        Director(
          directorId = doc.getInteger("directorId"),
          name = doc.getString("name")
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getDirectorById(directorId: String): Future[Option[Director]] = {
    val directorDocument = Document("directorId" -> directorId.toInt)

    Mongodbcollection.directorCollection.find(directorDocument).headOption().map {
      case Some(doc) =>
        Some(
          Director(
            directorId = doc.getInteger("directorId"),
            name = doc.getString("name")
          )
        )
      case None => None
    }
  }

  def addDirector(director: Director): Future[String] = {
    val directorDocument = BsonDocument(
      "directorId" -> BsonInt32(director.directorId),
      "name" -> BsonString(director.name)
    )

    Mongodbcollection.directorCollection.insertOne(directorDocument).toFuture().map(_ => s"Директор ${director.name} добавлен в базу данных.")
  }

  def deleteDirector(directorId: String): Future[String] = {
    val directorDocument = Document("directorId" -> directorId.toInt)
    Mongodbcollection.directorCollection.deleteOne(directorDocument).toFuture().map(_ => s"Директор с ID $directorId удален из базы данных.")
  }

  def updateDirector(directorId: String, updatedDirector: Director): Future[String] = {
    val filter = Document("directorId" -> directorId.toInt)

    val directorDocument = BsonDocument(
      "$set" -> BsonDocument(
        "directorId" -> BsonInt32(updatedDirector.directorId),
        "name" -> BsonString(updatedDirector.name)
      )
    )

    Mongodbcollection.directorCollection.updateOne(filter, directorDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о директоре с ID $directorId успешно обновлена."
      } else {
        s"Обновление информации о директоре с ID $directorId не выполнено. Возможно, директор не найден или произошла ошибка в базе данных."
      }
    }
  }
}