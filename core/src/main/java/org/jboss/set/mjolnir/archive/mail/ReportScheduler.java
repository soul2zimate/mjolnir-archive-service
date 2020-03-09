package org.jboss.set.mjolnir.archive.mail;

import org.jboss.logging.Logger;

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
    private MailingService mailingService;

    @Inject
    private MailBodyMessageProducer mailBodyMessageProducer;

    @Schedule(dayOfWeek="Sun", hour="0", persistent = false)
    public void sendMail() throws InterruptedException {
        mailingService.setFrom("sender@report.com");
        mailingService.setTo("receiver@report.com");
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
