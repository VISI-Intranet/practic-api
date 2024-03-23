package amqp

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.rabbitmq.client.{Channel, Connection, ConnectionFactory}

class AmqpActor(exchangeName: String,serviceName:String) extends Actor with ActorLogging {
  private var connection: Connection = _
  private var channel: Channel = _

  private var senderActor : ActorRef = _
  private var askerActor : ActorRef = _

  override def preStart(): Unit = {

    // Создаем соединение с RabbitMQ
    val factory = new ConnectionFactory()
    factory.setHost("localhost")
    factory.setPort(5672)
    factory.setUsername("guest")
    factory.setPassword("guest")
    connection = factory.newConnection()

    channel = connection.createChannel()
    log.info("Соединение с RabbitMQ установлено")

    senderActor = context.actorOf(SenderActor.props(channel,exchangeName), "sender")
    askerActor = context.actorOf(AskActor.props(channel,exchangeName, serviceName), "asker")
  }


  override def receive: Receive = {
    case msg@RabbitMQ.Tell(routingKey, content) =>
      senderActor forward msg

    case msg@RabbitMQ.Answer(routingKey,correlationId,content)=>
      senderActor forward msg

    case msg@RabbitMQ.Ask(routingKey, content) =>
      // Запрос передаем дальше
      askerActor forward msg

    case RabbitMQ.DeclareListener(queue,bind_routing_key,actorName, handle) =>
      // Создаем актора слушателья
      context.actorOf(ReceiverActor.props(channel,queue,exchangeName,bind_routing_key,handle),actorName)
  }

  override def postStop(): Unit = {
    // Закрываем соединение с RabbitMQ
    channel.close()
    connection.close()

    log.info("Соединение с RabbitMQ закрыто")
  }
}
