package object amqp {
  case class Message(body:String,routingKey:String,replyTo:String,correlationId:String)
}
