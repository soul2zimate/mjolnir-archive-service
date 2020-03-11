package org.jboss.set.mjolnir.archive.mail;

import javax.annotation.Resource;
import javax.faces.bean.ApplicationScoped;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;

@ApplicationScoped
public class MailingService {

    private String toAddress;
    private String fromAddress;
    private String subject;
    private String body;

    @Resource (mappedName="java:jboss/mail/Default")
    private Session mailSession;

    public void send() throws MessagingException {
        MimeBodyPart mailMessage = new MimeBodyPart();
        mailMessage.setContent(body, "text/html; charset=utf-8");

        MimeMultipart mailContent = new MimeMultipart();
        mailContent.addBodyPart(mailMessage);

        Message message = new MimeMessage(mailSession);
        message.setFrom(new InternetAddress(fromAddress));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
        message.setSubject(subject);
        message.setContent(mailContent);

        Transport.send(message);
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress= fromAddress;
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
