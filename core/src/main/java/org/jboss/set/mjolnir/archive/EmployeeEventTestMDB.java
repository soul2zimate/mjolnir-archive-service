package org.jboss.set.mjolnir.archive;

import org.jboss.ejb3.annotation.ResourceAdapter;
import org.jboss.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(name = "EmployeeEventTestMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "Consumer.mjolnir.employees-events.VirtualTopic.services.enterprise-iam.integration.event"), // physical name
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
@ResourceAdapter(value="activemq-rar.rar")
public class EmployeeEventTestMDB implements MessageListener {

    private final Logger logger = Logger.getLogger(EmployeeEventTestMDB.class);

    @Override
    public void onMessage(Message message) {
        // a TextMessage is expected here
        if (message instanceof TextMessage) {
            try {
                logger.info("Received text message: " + ((TextMessage) message).getText());
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        } else {
            logger.info("Unexpected message type: " + message.getClass().getName());
            logger.info("Received message: " + message.toString());
        }
    }
}
