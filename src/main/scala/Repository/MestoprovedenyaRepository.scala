package Repository

import org.mongodb.scala.Document
import org.mongodb.scala.bson.{BsonDocument, BsonInt32, BsonString}
import scala.concurrent.{ExecutionContext, Future}
import Connection._
import Model.Mestoprovedenya

class MestoProvedenyaRepository(implicit ec: ExecutionContext) {

  def getAllMestoProvedenya(): Future[List[Mestoprovedenya]] = {
    val futureMestoProvedenya = Mongodbcollection.mestoprovedenyaCollection.find().toFuture()

    futureMestoProvedenya.map { docs =>
      Option(docs).map(_.map { doc =>
        Mestoprovedenya(
          mestoId = doc.getInteger("mestoId"),
          director = doc.getString("director")
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getMestoProvedenyaById(mestoId: String): Future[Option[Mestoprovedenya]] = {
    val mestoProvedenyaDocument = Document("mestoId" -> mestoId.toInt)

    Mongodbcollection.mestoprovedenyaCollection.find(mestoProvedenyaDocument).headOption().map {
      case Some(doc) =>
        Some(
          Mestoprovedenya(
            mestoId = doc.getInteger("mestoId"),
            director = doc.getString("director")
          )
        )
      case None => None
    }
  }

  def addMestoProvedenya(mestoProvedenya: Mestoprovedenya): Future[String] = {
    val mestoProvedenyaDocument = BsonDocument(
      "mestoId" -> BsonInt32(mestoProvedenya.mestoId),
      "director" -> BsonString(mestoProvedenya.director)
    )

    Mongodbcollection.mestoprovedenyaCollection.insertOne(mestoProvedenyaDocument).toFuture().map(_ => s"Место проведения с ID ${mestoProvedenya.mestoId} добавлено в базу данных.")
  }

  def deleteMestoProvedenya(mestoId: String): Future[String] = {
    val mestoProvedenyaDocument = Document("mestoId" -> mestoId.toInt)
    Mongodbcollection.mestoprovedenyaCollection.deleteOne(mestoProvedenyaDocument).toFuture().map(_ => s"Место проведения с ID $mestoId удалено из базы данных.")
  }

  def updateMestoProvedenya(mestoId: String, updatedMestoProvedenya: Mestoprovedenya): Future[String] = {
    val filter = Document("mestoId" -> mestoId.toInt)

    val mestoProvedenyaDocument = BsonDocument(
      "$set" -> BsonDocument(
        "mestoId" -> BsonInt32(updatedMestoProvedenya.mestoId),
        "director" -> BsonString(updatedMestoProvedenya.director)
      )
    )

    Mongodbcollection.mestoprovedenyaCollection.updateOne(filter, mestoProvedenyaDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о месте проведения с ID $mestoId успешно обновлена."
      } else {
        s"Обновление информации о месте проведения с ID $mestoId не выполнено. Возможно, место проведения не найдено или произошла ошибка в базе данных."
      }
    }
  }
}