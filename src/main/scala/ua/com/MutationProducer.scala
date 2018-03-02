package ua.com

import javax.jms._

import org.apache.activemq.ActiveMQConnectionFactory
import ua.com.entity.Nucleotide
import ua.com.entity.Nucleotide.getRandomNucleo

object MutationProducer {
  val activeMqUrl: String = "tcp://localhost:61616"
  val topicName: String = "DNAString"
  val generator = new DNAGenerator
  val nucleoStream: Stream[Nucleotide] = generator.mutation(getRandomNucleo)

  def main(args: Array[String]): Unit = {
    val connectionFactory = new ActiveMQConnectionFactory(activeMqUrl)
    connectionFactory.setUseAsyncSend(true)
    val connection = connectionFactory.createConnection
        connection.setClientID("DNAMutator")
    connection.start()

    val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val topic: Topic = session.createTopic(topicName)
    val producer = session.createProducer(topic)
    producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT)
    def send (a: String) = {
      val textMessage = session.createTextMessage(a)
      println("Sending message...")

      producer.send(textMessage)
      println("Message sent: " + textMessage.getText + " to " + textMessage.getJMSDestination + " at " + textMessage.getJMSTimestamp)
         }

    nucleoStream take 10 foreach( n => send(n.toString))
    connection.close()
  }
}
