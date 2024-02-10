package Repository

import org.mongodb.scala.Document
import org.mongodb.scala.bson.{BsonDocument, BsonInt32, BsonString}
import scala.concurrent.{ExecutionContext, Future}
import Connection._
import Model._

class ZadachiRepository(implicit ec: ExecutionContext) {

  def getAllZadachi(): Future[List[Zadachi]] = {
    val futureZadachi = Mongodbcollection.zadachiCollection.find().toFuture()

    futureZadachi.map { docs =>
      Option(docs).map(_.map { doc =>
        Zadachi(
          zadachiId = doc.getInteger("zadachiId"),
          name = doc.getString("name")
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getZadachiById(zadachiId: String): Future[Option[Zadachi]] = {
    val zadachiDocument = Document("zadachiId" -> zadachiId.toInt)

    Mongodbcollection.zadachiCollection.find(zadachiDocument).headOption().map {
      case Some(doc) =>
        Some(
          Zadachi(
            zadachiId = doc.getInteger("zadachiId"),
            name = doc.getString("name")
          )
        )
      case None => None
    }
  }

  def addZadachi(zadachi: Zadachi): Future[String] = {
    val zadachiDocument = BsonDocument(
      "zadachiId" -> BsonInt32(zadachi.zadachiId),
      "name" -> BsonString(zadachi.name)
    )

    Mongodbcollection.zadachiCollection.insertOne(zadachiDocument).toFuture().map(_ => s"Задача ${zadachi.name} добавлена в базу данных.")
  }

  def deleteZadachi(zadachiId: String): Future[String] = {
    val zadachiDocument = Document("zadachiId" -> zadachiId.toInt)
    Mongodbcollection.zadachiCollection.deleteOne(zadachiDocument).toFuture().map(_ => s"Задача с ID $zadachiId удалена из базы данных.")
  }

  def updateZadachi(zadachiId: String, updatedZadachi: Zadachi): Future[String] = {
    val filter = Document("zadachiId" -> zadachiId.toInt)

    val zadachiDocument = BsonDocument(
      "$set" -> BsonDocument(
        "zadachiId" -> BsonInt32(updatedZadachi.zadachiId),
        "name" -> BsonString(updatedZadachi.name)
      )
    )

    Mongodbcollection.zadachiCollection.updateOne(filter, zadachiDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о задаче с ID $zadachiId успешно обновлена."
      } else {
        s"Обновление информации о задаче с ID $zadachiId не выполнено. Возможно, задача не найдена или произошла ошибка в базе данных."
      }
    }
  }
}