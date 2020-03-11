package org.jboss.set.mjolnir.archive.mail;

import org.jboss.logging.Logger;
import org.jboss.set.mjolnir.archive.configuration.Configuration;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.mail.MessagingException;
import java.sql.Timestamp;

@Singleton
public class ReportScheduler {

    private Logger logger = Logger.getLogger(getClass());

    private static final String SUBJECT = "User removals report ";

    @Inject
    private Configuration configuration;

    @Inject
    private MailingService mailingService;

    @Inject
    private MailBodyMessageProducer mailBodyMessageProducer;

    @Schedule(dayOfWeek="Sun", hour="0", persistent = false)
    public void sendMail() throws InterruptedException {
        mailingService.setFromAddress(configuration.getReportingEmail());
        mailingService.setToAddress(configuration.getReportingEmail());
        mailingService.setSubject(SUBJECT + new Timestamp(System.currentTimeMillis()));
        mailingService.setBody(mailBodyMessageProducer.composeMessageBody());

        try {
            mailingService.send();
            logger.infof("Report email sent successfully");
        } catch (MessagingException e) {
            logger.errorf(e, "Failure of report email sending");
        }
    }
}
