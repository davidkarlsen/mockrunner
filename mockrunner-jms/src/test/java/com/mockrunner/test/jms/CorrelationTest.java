package com.mockrunner.test.jms;

import com.mockrunner.jms.ConfigurationManager;
import com.mockrunner.jms.DestinationManager;
import com.mockrunner.mock.jms.MockConnectionFactory;
import com.mockrunner.mock.jms.MockQueue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.jms.*;


/**
 * Created by david on 11/07/15.
 */
@RunWith(JUnit4.class)
public class CorrelationTest
    extends Assert
{
    @Test
    public void issue24() throws JMSException {
        DestinationManager destinationManager = new DestinationManager();
        MockQueue testQueue = destinationManager.createQueue("TESTQUEUE");
        ConfigurationManager configurationManager = new ConfigurationManager();
        MockConnectionFactory mockConnectionFactory = new MockConnectionFactory(destinationManager,configurationManager);
        QueueSession session = mockConnectionFactory.createQueueConnection().createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer messageProducer = session.createProducer(testQueue);
        Message message = session.createTextMessage();
        final String correlationId = "someCorrId";
        message.setJMSCorrelationID(correlationId);
        messageProducer.send(message);

        String selector = "JMSCorrelationID = '" + correlationId + "'";

        Message receivedMessage = session.createReceiver(testQueue, selector).receive();
        assertNotNull(receivedMessage);
        assertSame( message, receivedMessage );
    }
}
