package ua.com

import javax.jms._

import org.apache.activemq.ActiveMQConnectionFactory
import org.slf4j.LoggerFactory

class Publisher(url: String, topicName: String, ID: String) {

  private val logger = LoggerFactory.getLogger(this.getClass)
  val connectionFactory = new ActiveMQConnectionFactory(url)
  connectionFactory.setUseAsyncSend(false)
  val connection: Connection = connectionFactory.createConnection
  connection.setClientID(ID)
  connection.start()

  val session: Session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
  val topic: Topic = session.createTopic(topicName)

  val publisher: MessageProducer = session.createProducer(topic)
  publisher.setDeliveryMode(DeliveryMode.PERSISTENT)

  def send(a: String): Unit = {
    val textMessage = session.createTextMessage(a)
    publisher.send(textMessage)
    // println(s"Sending message: ${textMessage.getText} to ${textMessage.getJMSDestination}")
  }

  def closeConnection(): Unit = {
    publisher.close()
  }
}
