package amqp

sealed trait RabbitMQ


object RabbitMQ {
  case class Tell(routingKey: String,content: String) extends RabbitMQ
  case class Ask(routingKey: String,content: String) extends RabbitMQ
  case class Answer(routingKey: String,correlationId:String,content: String)
  case class DeclareListener(queue: String,bind_routing_key:String,actorName:String,handle: Message => Unit) extends RabbitMQ
}

