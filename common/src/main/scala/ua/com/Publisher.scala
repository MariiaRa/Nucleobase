package ua.com

import javax.jms._
import com.typesafe.config._
import org.apache.activemq.ActiveMQConnectionFactory

class Publisher {

  val confCommon: Config = ConfigFactory.load("reference.conf")
  val url: String = confCommon.getString("common-lib.activeMQ.url")
  val topicName: String = confCommon.getString("common-lib.activeMQ.topicName")

  val myConf: Config = ConfigFactory.load()
  val ID: String = myConf.getString("DNAProducer.ID")

  val connectionFactory = new ActiveMQConnectionFactory(url)
  connectionFactory.setUseAsyncSend(false)
  val connection: Connection = connectionFactory.createConnection
  connection.setClientID(ID)
  connection.start()

  val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
  val topic: Topic = session.createTopic(topicName)
  val publisher = session.createProducer(topic)
  publisher.setDeliveryMode(DeliveryMode.PERSISTENT) //

  def send(a: String): Unit = {
    val textMessage = session.createTextMessage(a)
    publisher.send(textMessage)
    println("Message sent: " + textMessage.getText + " to " + textMessage.getJMSDestination + " at " + textMessage.getJMSTimestamp)
  }

  def closeConnection(): Unit = {
    connection.close()
  }
}
