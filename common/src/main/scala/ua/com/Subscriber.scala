package ua.com

import javax.jms._

import org.apache.activemq.ActiveMQConnectionFactory
import org.slf4j.LoggerFactory

class Subscriber(url: String, topicName: String, ID: String) {
  private val logger = LoggerFactory.getLogger(this.getClass)
  val connectionFactory = new ActiveMQConnectionFactory(url)
  val connection: Connection = connectionFactory.createConnection
  connection.setClientID(ID)
  connection.start()

  val session: Session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE)
  val topic: Topic = session.createTopic(topicName)
  val subscriber: TopicSubscriber = session.createDurableSubscriber(topic, "Durable_Subscriber")

  val sb = new StringBuilder
  // size of generated DNA string
  val batchSize = 100

  /**
    *
    * @return read messages from topic (nucleotides) and generate DNA String of predefined size to feed it to alpha algorithm
    */
  def processBatch(): String = {
    val sb = new StringBuilder
    while (sb.toString().length < batchSize) {
      val message: Message = subscriber.receiveNoWait()
      if (message.isInstanceOf[TextMessage]) {
        val textMessage: TextMessage = message.asInstanceOf[TextMessage]
        sb.append(textMessage.getText)
        message.acknowledge()
      }
    }
    sb.toString()
  }

  /**
    *
    * @return messages from topic
    */

  def getCommand(): Option[String] = {
    val message: Message = subscriber.receiveNoWait()
    if (message.isInstanceOf[TextMessage] && message != null) {
      val textMessage: TextMessage = message.asInstanceOf[TextMessage]
      logger.info("Received message: " + textMessage.getText)
      message.acknowledge()
      Some(textMessage.getText)
    } else {
      None
    }
  }

  def closeConnection(): Unit = {
    connection.close()
  }

}