package org.jboss.set.mjolnir.archive.umb;

import org.jboss.ejb3.annotation.ResourceAdapter;
import org.jboss.logging.Logger;
import org.jboss.set.mjolnir.archive.domain.RegisteredUser;
import org.jboss.set.mjolnir.archive.domain.repositories.RegisteredUserRepositoryBean;
import org.jboss.set.mjolnir.archive.domain.repositories.RemovalLogRepositoryBean;
import org.jboss.set.mjolnir.archive.ldap.LdapScanningBean;
import org.json.JSONObject;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.Optional;

@MessageDriven(name = "EmployeeOffBoardEventsMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "Consumer.mjolnir.employees-events.VirtualTopic.services.enterprise-iam.integration.event"), // physical name
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")})
@ResourceAdapter(value = "activemq-rar.rar")
public class EmployeeOffBoardEventsMDB implements MessageListener {

    private static final String OFF_BOARD_EVENT = "offboard";

    private final Logger logger = Logger.getLogger(getClass());

    @Inject
    private RegisteredUserRepositoryBean userRepositoryBean;

    @Inject
    private LdapScanningBean ldapScanningBean;

    @Inject
    private RemovalLogRepositoryBean logRepositoryBean;

    public void onMessage(Message rcvMessage) {
        try {
            if (rcvMessage instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) rcvMessage;
                processMessage(textMessage.getText());
            } else {
                logger.warnf("Message of wrong type: %s", rcvMessage.getClass().getName());
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    private void processMessage(String message) {
        JSONObject jsonObject = new JSONObject(message);
        String event = jsonObject.getString("event");

        if (OFF_BOARD_EVENT.equals(event)) {
            JSONObject person = jsonObject.getJSONObject("person");
            String kerberosName = person.getString("uid");

            logger.infof("Received offboard event for user %s.", kerberosName);

            Optional<RegisteredUser> registeredUser = userRepositoryBean.findByKrbUsername(kerberosName);
            if (registeredUser.isPresent()) {
                RegisteredUser user = registeredUser.get();
                if (user.isWhitelisted()) {
                    logger.infof("Skipping whitelisted user %s.", user.getKerberosName());
                    logRepositoryBean.logMessage(String.format("Offboard event: Skipping whitelisted user %s.", user.getKerberosName()));
                } else {
                    logger.infof("Creating removal for user %s.", user.getKerberosName());
                    logRepositoryBean.logMessage(String.format("Offboard event: Creating removal for user %s.", user.getKerberosName()));
                    ldapScanningBean.createUserRemoval(user.getKerberosName());
                }
            } else {
                logger.infof("User %s is not registered.", kerberosName);
                logRepositoryBean.logMessage(String.format("Offboard event: User %s is not registered.", kerberosName));
            }
        }
    }
}
