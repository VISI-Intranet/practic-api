package Model
import org.json4s.JsonAST.JString
import org.json4s.{CustomSerializer, DefaultFormats, Formats, MappingException}

import java.util.Date
object Status extends Enumeration {
  type Status = Value
  val Запланировано,В_процессе,Зваершено = Value
}
object Tip extends Enumeration {
  type Tip = Value
  val Производственная,Преддипломная = Value
}
object Ocenka extends Enumeration {
  type Ocenka = Value
  val Хорошо,Отлично,Удовлетворительно,Неудовлетворительно = Value
}
case class Practice (
                      practiceId:Int,
                      name:String,
                      opicania:String,
                      prodolzhotelnost:String,
                      data_nachalo:Date,
                      data_okanchanya:Date,
                      mesto_provedenya:String,
                      zadachi:List[Int],
                      prepodavatel:List[Int],
                      spisok_studentov:List[Int],
                      status:Status.Status,
                      tip: Tip.Tip,
                      raspicnya:Int,
                      ocenka:Ocenka.Ocenka,
                      spisok_dokumentov:List[Int]

                    )

object JsonFormats {
  val Enumm = new CustomSerializer[Enumeration#Value](format => (
    {
      case JString(s) =>
        if (Status.values.exists(_.toString == s)) {
          Status.withName(s)
        } else if (Tip.values.exists(_.toString == s)) {
          Tip.withName(s)
        }
          else if (Ocenka.values.exists(_.toString == s)) {
            Ocenka.withName(s)
        } else {
          throw new MappingException(s"Unknown enumeration value: $s")
        }
      case value =>
        throw new MappingException(s"Can't convert $value to Enumeration")
    },
    {
      case enumValue: Enumeration#Value =>
        JString(enumValue.toString)
    }
  ))

  implicit val formats: Formats = DefaultFormats + Enumm
}
