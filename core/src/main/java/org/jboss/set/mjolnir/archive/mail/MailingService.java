package org.jboss.set.mjolnir.archive.mail;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;

@Stateless
public class MailingService {

    private String to;

    private String from;

    private String subject;

    private String body;

    //TODO: Add SMTP server
    @Resource (name="")
    private Session mailSession;

    public void send() throws MessagingException {
        MimeBodyPart mailMessage = new MimeBodyPart();
        mailMessage.setContent(body, "text/html; charset=utf-8");

        MimeMultipart mailContent = new MimeMultipart();
        mailContent.addBodyPart(mailMessage);

        Message message = new MimeMessage(mailSession);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setContent(mailContent);

        Transport.send(message);
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
