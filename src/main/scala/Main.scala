import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.mongodb.scala.MongoClient
import Repository._
import Routing._
import Model._
import scala.concurrent.{ExecutionContextExecutor, Future}
import java.util.Date
import akka.http.scaladsl.Http
object Main extends App {

  implicit val system: ActorSystem = ActorSystem("MyAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // Подключение к базе данных
  val client = MongoClient()
  implicit val db = client.getDatabase("UniverFacultet")

  implicit val practiceRepository = new PracticeRepository()
  implicit val directorRepository = new DirectorRepository()
  implicit val zadachiRepository = new ZadachiRepository()
  implicit val mestoProvedenyaRepository = new MestoProvedenyaRepository()

  val practiceRoutes = new PracticeRoutes()
  val directorRoutes = new DirectorRoutes()
  val zadachiRoutes = new ZadachiRoutes()
  val mestoProvedenyaRoutes = new MestoProvedenyaRoutes()


  // Старт сервера
  private val bindingFuture = Http().bindAndHandle(practiceRoutes.route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/")

  // Остановка сервера при завершении приложения
  sys.addShutdownHook {
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}