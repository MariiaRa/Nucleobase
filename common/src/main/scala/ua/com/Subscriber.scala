package ua.com

import javax.jms._

import org.apache.activemq.ActiveMQConnectionFactory

class Subscriber(url: String, topicName: String, ID: String) {
  // @throws(classOf[JMSException])
  val connectionFactory = new ActiveMQConnectionFactory(url)
  val connection: Connection = connectionFactory.createConnection
  connection.setClientID(ID)
  connection.start()

  val session: Session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
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
        // println("Message received from producer: " + textMessage.getText)
        sb.append(textMessage.getText)
        // Once we have successfully processed the message, send an acknowledge back to ActiveMQ
        message.acknowledge
      }
    }
    println("DNA String: " + sb.toString())
    sb.toString()
  }
  // val list = new mutable.MutableList[String]
  def closeConnection(): Unit = {
    connection.close()
  }
}
