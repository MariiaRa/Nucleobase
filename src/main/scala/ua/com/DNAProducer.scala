package ua.com

import javax.jms._

import org.apache.activemq.ActiveMQConnectionFactory
import ua.com.entity.Nucleotide
import ua.com.entity.Nucleotide.getRandomNucleo

object DNAProducer {
  val activeMqUrl: String = "tcp://localhost:61616"
  val topicName: String = "DNAString"
  val generator = new DNAGenerator
  val DNAStream: Stream[Nucleotide] = generator.nucleo(getRandomNucleo, false)

  def main(args: Array[String]): Unit = {
    val connectionFactory = new ActiveMQConnectionFactory(activeMqUrl)
    connectionFactory.setUseAsyncSend(true)
    val connection = connectionFactory.createConnection
    connection.setClientID("DNAProducer")
    connection.start()

    val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val topic: Topic = session.createTopic(topicName)
    val producer = session.createProducer(topic)
    producer.setDeliveryMode(DeliveryMode.PERSISTENT)

    def send(a: String) = {
      val textMessage = session.createTextMessage(a)
      println("Sending message...")
      producer.send(textMessage)
      println("Message sent: " + textMessage.getText + " to " + textMessage.getJMSDestination + " at " + textMessage.getJMSTimestamp)
    }

    DNAStream take 10 foreach (n => send(n.toString))
    connection.close()
  }
}
