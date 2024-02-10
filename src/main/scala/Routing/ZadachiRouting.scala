package Routing

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, Formats, jackson}
import Repository._
import Model._

class ZadachiRoutes(implicit val zadachiRepository: ZadachiRepository) extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats: Formats = DefaultFormats

  val route =
    pathPrefix("zadachi") {
      concat(
        pathEnd {
          concat(
            get {
              complete(zadachiRepository.getAllZadachi())
            },
            post {
              entity(as[Zadachi]) { zadachi =>
                complete(zadachiRepository.addZadachi(zadachi))
              }
            }
          )
        },
        path(Segment) { zadachiId =>
          concat(
            get {
              complete(zadachiRepository.getZadachiById(zadachiId))
            },
            put {
              entity(as[Zadachi]) { updatedZadachi =>
                complete(zadachiRepository.updateZadachi(zadachiId, updatedZadachi))
              }
            },
            delete {
              complete(zadachiRepository.deleteZadachi(zadachiId))
            }
          )
        }
      )
    }
}
