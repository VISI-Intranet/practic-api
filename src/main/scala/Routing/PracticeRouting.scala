package Routing

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, Formats, jackson}
import Repository._
import Model._

class PracticeRoutes(implicit val practiceRepository: PracticeRepository) extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats: Formats = JsonFormats.formats

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
                complete(practiceRepository.addPractice(practice))
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