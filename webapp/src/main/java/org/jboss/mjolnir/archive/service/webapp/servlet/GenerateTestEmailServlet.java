package org.jboss.mjolnir.archive.service.webapp.servlet;

import org.jboss.logging.Logger;
import org.jboss.set.mjolnir.archive.configuration.Configuration;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/generate-test-email")
public class GenerateTestEmailServlet extends HttpServlet {

    private static final String SUBJECT = "Test email from mjolnir-archive-service";
    private static final String BODY = "Please ignore.";

    private final Logger logger = Logger.getLogger(getClass());

    @Resource(mappedName="java:jboss/mail/Default")
    private Session mailSession;

    @Inject
    private Configuration configuration;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String reportingEmail = configuration.getReportingEmail();
            logger.infof("Sending test email to %s", reportingEmail);

            MimeBodyPart mailMessage = new MimeBodyPart();
            mailMessage.setContent(BODY, "text/html; charset=utf-8");

            MimeMultipart mailContent = new MimeMultipart();
            mailContent.addBodyPart(mailMessage);

            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(reportingEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(reportingEmail));
            message.setSubject(SUBJECT);
            message.setContent(mailContent);

            Transport.send(message);
        } catch (MessagingException e) {
            throw new ServletException(e);
        }
    }
}
