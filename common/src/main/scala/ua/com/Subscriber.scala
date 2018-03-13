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

  /* def receiveMessage(): Unit = {
     val listener: MessageListener = new MessageListener {
       def onMessage(message: Message): Unit = {
         try {
           if (message.isInstanceOf[TextMessage]) {
             val textMessage: TextMessage = message.asInstanceOf[TextMessage]
             println("Message received: " + textMessage.getText)
             //Once we have successfully processed the message, send an acknowledge back to ActiveMQ
             message.acknowledge()
           }
         }
         catch {
           case ex: Exception => println(ex.getMessage)
         }
       }
     }
     durableSubscriber.setMessageListener(listener)
   }*/

  val sb = new StringBuilder
  val batchSize = 100

  def processBatch(): String = {
    val sb = new StringBuilder
    while (sb.toString().length < batchSize) {
      val message: Message = subscriber.receiveNoWait()
      if (message.isInstanceOf[TextMessage]) {
        val textMessage: TextMessage = message.asInstanceOf[TextMessage]
        sb.append(textMessage.getText)
        message.acknowledge
      }
    }
    sb.toString()
  }

  def getCommand(): Option[String] = {
    val message: Message = subscriber.receiveNoWait()
    if (message.isInstanceOf[TextMessage] && message != null) {
      val textMessage: TextMessage = message.asInstanceOf[TextMessage]
      logger.info("Received message: " + textMessage.getText())
      message.acknowledge
      Some(textMessage.getText())
     } else {
      None
    }
  }

  def closeConnection(): Unit = {
    connection.close()
  }

}