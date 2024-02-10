package Routing

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, Formats, jackson}
import Repository._
import Model._

class DirectorRoutes(implicit val directorRepository: DirectorRepository) extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats: Formats = DefaultFormats

  val route =
    pathPrefix("directors") {
      concat(
        pathEnd {
          concat(
            get {
              complete(directorRepository.getAllDirectors())
            },
            post {
              entity(as[Director]) { director =>
                complete(directorRepository.addDirector(director))
              }
            }
          )
        },
        path(Segment) { directorId =>
          concat(
            get {
              complete(directorRepository.getDirectorById(directorId))
            },
            put {
              entity(as[Director]) { updatedDirector =>
                complete(directorRepository.updateDirector(directorId, updatedDirector))
              }
            },
            delete {
              complete(directorRepository.deleteDirector(directorId))
            }
          )
        }
      )
    }
}

