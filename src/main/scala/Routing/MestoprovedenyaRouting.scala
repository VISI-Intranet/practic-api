package Routing

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, Formats, jackson}
import Repository._
import Model._

class MestoProvedenyaRoutes(implicit val mestoProvedenyaRepository: MestoProvedenyaRepository) extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats: Formats = DefaultFormats

  val route =
    pathPrefix("mesto_provedenya") {
      concat(
        pathEnd {
          concat(
            get {
              complete(mestoProvedenyaRepository.getAllMestoProvedenya())
            },
            post {
              entity(as[Mestoprovedenya]) { mestoProvedenya =>
                complete(mestoProvedenyaRepository.addMestoProvedenya(mestoProvedenya))
              }
            }
          )
        },
        path(Segment) { mestoId =>
          concat(
            get {
              complete(mestoProvedenyaRepository.getMestoProvedenyaById(mestoId))
            },
            put {
              entity(as[Mestoprovedenya]) { updatedMestoProvedenya =>
                complete(mestoProvedenyaRepository.updateMestoProvedenya(mestoId, updatedMestoProvedenya))
              }
            },
            delete {
              complete(mestoProvedenyaRepository.deleteMestoProvedenya(mestoId))
            }
          )
        }
      )
    }
}