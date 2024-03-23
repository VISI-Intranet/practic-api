package amqp

import akka.actor.{Actor, ActorLogging, Props}
import com.rabbitmq.client.{AMQP, Channel, DefaultConsumer, Envelope}


class ReceiverActor(
                     channel: Channel,
                     queueName: String,
                     exchangeName:String,
                     bind_routing_key:String,
                     handle:Message=>Unit) extends Actor with ActorLogging {
  private val consumer = new DefaultConsumer(channel) {
    override def handleDelivery(consumerTag: String,
                                envelope: Envelope,
                                properties: AMQP.BasicProperties,
                                body: Array[Byte]): Unit = {
      val message = new String(body, "UTF-8")
      log.info(s"Пришло сообшение по ключу: ${envelope.getRoutingKey}. Тело запроса: $message")
      handle(Message(message,envelope.getRoutingKey,properties.getReplyTo,properties.getCorrelationId))
    }
  }

  channel.queueDeclare(queueName, false, false, false, null)
  channel.queueBind(queueName, exchangeName, bind_routing_key)
  
  consumerTag = channel.basicConsume(queueName, true, consumer)
  
  private var consumerTag:String = _

  override def receive: Receive = {
    // Начинаем прослушивание очереди
    case "Listen" =>
      consumerTag = channel.basicConsume(queueName, true, consumer)
    case "Unlisted" =>
      channel.basicCancel(consumerTag)
  }
}

object ReceiverActor {
  def props(channel: Channel, queueName: String, exchangeName: String,
            bind_routing_key: String, handle: Message=>Unit): Props =
    Props(new ReceiverActor(channel, queueName,exchangeName,bind_routing_key, handle))
}