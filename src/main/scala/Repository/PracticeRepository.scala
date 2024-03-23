package Repository

import org.mongodb.scala.model.Filters.{equal, regex}
import org.mongodb.scala.model.Updates.{addToSet, combine, set}
import org.mongodb.scala.result.UpdateResult

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonInt32, BsonString}
import org.mongodb.scala.Document
import Connection._
import Model._
import scala.util.Try
import java.text.SimpleDateFormat

class PracticeRepository(implicit ec: ExecutionContext) {

  def getAllPractices(): Future[List[Practice]] = {
    val futurePractices = Mongodbcollection.practiceCollection.find().toFuture()

    futurePractices.map { docs =>
      Option(docs).map(_.map { doc =>
        Practice(
          practiceId = doc.getInteger("practiceId"),
          name = doc.getString("name"),
          opicania = doc.getString("opicania"),
          prodolzhotelnost = doc.getString("prodolzhotelnost"),
          data_nachalo = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(doc.getString("data_nachalo")),
          data_okanchanya = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(doc.getString("data_okanchanya")),
          mesto_provedenya = doc.getString("mesto_provedenya"),
          zadachi = Option(doc.getList("zadachi", classOf[Integer])).map(_.asScala.toList.map(_.toInt)).getOrElse(List.empty),
          prepodavatel = Option(doc.getList("prepodavatel", classOf[Integer])).map(_.asScala.toList.map(_.toInt)).getOrElse(List.empty),
          spisok_studentov = Option(doc.getList("spisok_studentov", classOf[Integer])).map(_.asScala.toList.map(_.toInt)).getOrElse(List.empty),
          status = Status.withName(doc.getString("status")),
          tip = Tip.withName(doc.getString("tip")),
          raspicnya = Option(doc.getList("raspicnya", classOf[Integer])).map(_.asScala.toList.map(_.toInt)).getOrElse(List.empty),
          ocenka = Ocenka.withName(doc.getString("ocenka")),
          spisok_dokumentov = Option(doc.getList("spisok_dokumentov", classOf[Integer])).map(_.asScala.toList.map(_.toInt)).getOrElse(List.empty),

        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getPracticeById(practiceId: String): Future[Option[Practice]] = {
    val practiceDocument = Document("practiceId" -> practiceId.toInt)

    Mongodbcollection.practiceCollection.find(practiceDocument).headOption().map {
      case Some(doc) =>
        Some(
          Practice(
            practiceId = doc.getInteger("practiceId"),
            name = doc.getString("name"),
            opicania = doc.getString("opicania"),
            prodolzhotelnost = doc.getString("prodolzhotelnost"),
            data_nachalo = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(doc.getString("data_nachalo")),
            data_okanchanya = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(doc.getString("data_okanchanya")),
            mesto_provedenya = doc.getString("mesto_provedenya"),
            zadachi = Option(doc.getList("zadachi", classOf[Integer])).map(_.asScala.toList.map(_.toInt)).getOrElse(List.empty),
            prepodavatel = Option(doc.getList("prepodavatel", classOf[Integer])).map(_.asScala.toList.map(_.toInt)).getOrElse(List.empty),
            spisok_studentov = Option(doc.getList("spisok_studentov", classOf[Integer])).map(_.asScala.toList.map(_.toInt)).getOrElse(List.empty),
            status = Status.withName(doc.getString("status")),
            tip = Tip.withName(doc.getString("tip")),
            raspicnya = Option(doc.getList("raspicnya", classOf[Integer])).map(_.asScala.toList.map(_.toInt)).getOrElse(List.empty),
            ocenka = Ocenka.withName(doc.getString("ocenka")),
            spisok_dokumentov = Option(doc.getList("spisok_dokumentov", classOf[Integer])).map(_.asScala.toList.map(_.toInt)).getOrElse(List.empty)
          )
        )
      case None => None
    }
  }
  def Polnyipracticfiltr(param: String): Future[List[Practice]] = {
    val keyValue = param.split("=")

    if (keyValue.length == 2) {
      val key = keyValue(0)
      val value = keyValue(1)
      val facultyDocument = Document(key -> value)
      Mongodbcollection.practiceCollection
        .find(facultyDocument)
        .toFuture()
        .map { docs =>
          docs.map { doc =>
            Practice(
              practiceId = doc.getInteger("practiceId"),
              name = doc.getString("name"),
              opicania = doc.getString("opicania"),
              prodolzhotelnost = doc.getString("prodolzhotelnost"),
              data_nachalo = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(doc.getString("data_nachalo")),
              data_okanchanya = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(doc.getString("data_okanchanya")),
              mesto_provedenya = doc.getString("mesto_provedenya"),
              zadachi = Option(doc.getList("zadachi", classOf[Integer])).map(_.asScala.toList.map(_.toInt)).getOrElse(List.empty),
              prepodavatel = Option(doc.getList("prepodavatel", classOf[Integer])).map(_.asScala.toList.map(_.toInt)).getOrElse(List.empty),
              spisok_studentov = Option(doc.getList("spisok_studentov", classOf[Integer])).map(_.asScala.toList.map(_.toInt)).getOrElse(List.empty),
              status = Status.withName(doc.getString("status")),
              tip = Tip.withName(doc.getString("tip")),
              raspicnya = Option(doc.getList("raspicnya", classOf[Integer])).map(_.asScala.toList.map(_.toInt)).getOrElse(List.empty),
              ocenka = Ocenka.withName(doc.getString("ocenka")),
              spisok_dokumentov = Option(doc.getList("spisok_dokumentov", classOf[Integer])).map(_.asScala.toList.map(_.toInt)).getOrElse(List.empty)
            )
          }.toList
        }
    } else {
      // Обработка некорректного ввода
      Future.failed(new IllegalArgumentException("Неверный формат параметра"))
    }
  }
  def addPractice(practice: Practice): Future[String] = {
    val practiceDocument = BsonDocument(
      "practiceId" -> BsonInt32(practice.practiceId),
      "name" -> BsonString(practice.name),
      "opicania" -> BsonString(practice.opicania),
      "prodolzhotelnost" -> BsonString(practice.prodolzhotelnost),
      "data_nachalo" -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(practice.data_nachalo) ,
      "data_okanchanya" -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(practice.data_okanchanya) ,
      "mesto_provedenya" -> BsonString(practice.mesto_provedenya),
      "zadachi" -> BsonArray(practice.zadachi.map(BsonInt32(_))),
      "prepodavatel" -> BsonArray(practice.prepodavatel.map(BsonInt32(_))),
      "spisok_studentov" -> BsonArray(practice.spisok_studentov.map(BsonInt32(_))),
      "status" -> BsonString(practice.status.toString),
      "tip" -> BsonString(practice.tip.toString),
      "raspicnya" -> BsonArray(practice.raspicnya.map(BsonInt32(_))),
      "ocenka" -> BsonString(practice.ocenka.toString),
      "spisok_dokumentov" -> BsonArray(practice.spisok_dokumentov.map(BsonInt32(_)))
    )

    Mongodbcollection.practiceCollection.insertOne(practiceDocument).toFuture().map(_ => s"${practice.practiceId}")
  }

  def deletePractice(practiceId: String): Future[String] = {
    val practiceDocument = Document("practiceId" -> practiceId.toInt)
    Mongodbcollection.practiceCollection.deleteOne(practiceDocument).toFuture().map(_ => s"Практика с ID $practiceId удалена из базы данных.")
  }

  def updatePractice(practiceId: String, updatedPractice: Practice): Future[String] = {
    val filter = Document("practiceId" -> practiceId.toInt)

    val practiceDocument = BsonDocument(
      "$set" -> BsonDocument(
        "practiceId" -> BsonInt32(updatedPractice.practiceId),
        "name" -> BsonString(updatedPractice.name),
        "opicania" -> BsonString(updatedPractice.opicania),
        "prodolzhotelnost" -> BsonString(updatedPractice.prodolzhotelnost),
        "data_nachalo" -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(updatedPractice.data_nachalo),
        "data_okanchanya" ->  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(updatedPractice.data_okanchanya),
        "mesto_provedenya" -> BsonString(updatedPractice.mesto_provedenya),
        "zadachi" -> BsonArray(updatedPractice.zadachi.map(BsonInt32(_))),
        "prepodavatel" -> BsonArray(updatedPractice.prepodavatel.map(BsonInt32(_))),
        "spisok_studentov" -> BsonArray(updatedPractice.spisok_studentov.map(BsonInt32(_))),
        "status" -> BsonString(updatedPractice.status.toString),
        "tip" -> BsonString(updatedPractice.tip.toString),
        "raspicnya" -> BsonArray(updatedPractice.raspicnya.map(BsonInt32(_))),
        "ocenka" -> BsonString(updatedPractice.ocenka.toString),
        "spisok_dokumentov" -> BsonArray(updatedPractice.spisok_dokumentov.map(BsonInt32(_)))      )
    )

    Mongodbcollection.practiceCollection.updateOne(filter, practiceDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о практике с ID $practiceId успешно обновлена."
      } else {
        s"Обновление информации о практике с ID $practiceId не выполнено. Возможно, практика не найдена или произошла ошибка в базе данных."
      }


    }

  }

  def addPracticeWithRaspisania(practice: Practice, fields: Document): Future[String] = {

    val practiceDocument = BsonDocument(
      "practiceId" -> BsonInt32(practice.practiceId),
      "name" -> BsonString(practice.name),
      "opicania" -> BsonString(practice.opicania),
      "prodolzhotelnost" -> BsonString(practice.prodolzhotelnost),
      "data_nachalo" -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(practice.data_nachalo),
      "data_okanchanya" -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(practice.data_okanchanya),
      "mesto_provedenya" -> BsonString(practice.mesto_provedenya),
      "zadachi" -> BsonArray(practice.zadachi.map(BsonInt32(_))),
      "prepodavatel" -> BsonArray(practice.prepodavatel.map(BsonInt32(_))),
      "spisok_studentov" -> BsonArray(practice.spisok_studentov.map(BsonInt32(_))),
      "status" -> BsonString(practice.status.toString),
      "tip" -> BsonString(practice.tip.toString),
      "raspicnya" -> BsonArray(practice.raspicnya.map(BsonInt32(_))),
      "ocenka" -> BsonString(practice.ocenka.toString),
      "spisok_dokumentov" -> BsonArray(practice.spisok_dokumentov.map(BsonInt32(_))),
      "scheduleDocument" -> fields
    )

    Mongodbcollection.practiceCollection.insertOne(practiceDocument).toFuture().map(_ => s"${practice.practiceId}")
  }
}