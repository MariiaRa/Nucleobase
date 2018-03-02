package ua.com

import javax.jms._

import org.apache.activemq.ActiveMQConnectionFactory

object Consumer {
  val activeMqUrl: String = "tcp://localhost:61616"
  val topicName: String = "DNAString"

  def main(args: Array[String]): Unit = {
    val connectionFactory = new ActiveMQConnectionFactory(activeMqUrl)
    val connection = connectionFactory.createConnection
    connectionFactory.setUseAsyncSend(true)
    connection.setClientID("Consumer")
    connection.start

    println("Started")

    val session: Session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE)

    val topic: Topic = session.createTopic(topicName)

    val durableSubscriber: TopicSubscriber = session.createDurableSubscriber(topic, "Test_Durable_Subscriber")

    val listener: MessageListener = new MessageListener {
      def onMessage(message: Message): Unit = {
        try {
          if (message.isInstanceOf[TextMessage]) {
            val textMessage: TextMessage = message.asInstanceOf[TextMessage]
            println("Message received from producer: '" + textMessage.getText + "'")
            // Once we have successfully processed the message, send an acknowledge back to ActiveMQ
            message.acknowledge
          }
        }
        catch {
          case je: JMSException => {
            println(je.getMessage)
          }
        }
      }
    }
    durableSubscriber.setMessageListener(listener)
  }
}
