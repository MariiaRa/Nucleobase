package ua.com

import javax.jms._

import org.apache.activemq.ActiveMQConnectionFactory

class Publisher(url: String, topicName: String, ID: String) {

  val connectionFactory = new ActiveMQConnectionFactory(url)
  connectionFactory.setUseAsyncSend(false)
  val connection: Connection = connectionFactory.createConnection
  connection.setClientID(ID)
  connection.start()

  val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
  val topic: Topic = session.createTopic(topicName)

  val publisher = session.createProducer(topic)
  publisher.setDeliveryMode(DeliveryMode.PERSISTENT)

  def send(a: String): Unit = {
    try {
      val textMessage = session.createTextMessage(a)
      publisher.send(textMessage)
      println("Message sent: " + textMessage.getText + " to " + textMessage.getJMSDestination)
    } catch {
      case ex: IllegalStateException => {
        val publisher = session.createProducer(topic)
        publisher.setDeliveryMode(DeliveryMode.PERSISTENT)
        val textMessage = session.createTextMessage(a)
        publisher.send(textMessage)
        println("Message sent: " + textMessage.getText + " to " + textMessage.getJMSDestination)
      }
    }
  }

  def closeConnection(): Unit = {
    publisher.close()
  }
}
