package Routing

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, Formats, jackson}
import Repository._
import Model._
import akka.actor.ActorSystem
import amqp.RabbitMQ
import akka.pattern._
import akka.util.Timeout
import org.json4s.jackson.JsonMethods._
import org.mongodb.scala.bson.Document
import io.circe._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success}

class PracticeRoutes(implicit val practiceRepository: PracticeRepository, system: ActorSystem) extends Json4sSupport {
  implicit val wx: ExecutionContext = scala.concurrent.ExecutionContext.global
  implicit val serialization = jackson.Serialization
  implicit val formats: Formats = JsonFormats.formats
  implicit val timeout = Timeout(5 seconds)

  val amqpActor = system.actorSelection("user/amqpActor")

  val route =
    pathPrefix("practices") {
      concat(
        get {
          parameter("param") { param =>
            complete(practiceRepository.Polnyipracticfiltr(param))
          }
        },
        pathEnd {
          concat(
            get {
              complete(practiceRepository.getAllPractices())
            },
            post {
              entity(as[Practice]) { practice =>
                val requestJson =
                  s"""{
                     |"actionClass":"request",
                     |"routingKey":"univer.schedule_api.post",
                     |"body":{
                     |  "id":"${practice.raspicnya}"
                     | }
                     |}""".stripMargin
                val scheduleResponse = (amqpActor ? RabbitMQ.Ask("univer.schedule_api.post",requestJson))
                onComplete(scheduleResponse) {
                  case Success(schedule_json: String) =>
                    println(schedule_json)
                    val jsonString = schedule_json
                    val body = parse(jsonString) \ "body"
                    val scheduleId = (body \ "scheduleId").extract[String]
                    val dayOfWeek = (body \ "dayOfWeek").extract[String]
                    val startTime = (body \ "startTime").extract[String]
                    val endTime = (body \ "endTime").extract[String]
                    val semester = (body \ "semestr").extract[String]

                    val doc: Document = Document(
                      "scheduleId" -> scheduleId,
                      "dayOfWeek" -> dayOfWeek,
                      "startTime" -> startTime,
                      "endTime" -> endTime,
                      "semester" -> semester
                    )
                    onComplete(practiceRepository.addPracticeWithRaspisania(practice, doc)) {
                      case Success(value) =>
                        complete("Практика " + value + " добавлено в базу")
                      case Failure(exception) =>
                        complete(exception.getMessage)
                    }
                  case Failure(_) =>
                    complete("Ошибка при получении расписания")
                }
              }
            }
          )
        },
        path(Segment) { practiceId =>
          concat(
            get {
              complete(practiceRepository.getPracticeById(practiceId))
            },
            put {
              entity(as[Practice]) { updatedPractice =>
                complete(practiceRepository.updatePractice(practiceId, updatedPractice))
              }
            },
            delete {
              complete(practiceRepository.deletePractice(practiceId))
            }
          )
        }
      )
    }
}