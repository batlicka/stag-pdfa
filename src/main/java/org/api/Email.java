package org.api;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class Email {
    private String smtp;
    private Properties prop = new Properties();
    private String user = "v_vagunda@utb.cz";
    private String pass = "portal35";
    private String from = "v_vagunda@utb.cz";
    private String to = "v_vagunda@utb.cz";
    private String smtpServer = "smtp.utb.cz";

    //https://www.baeldung.com/java-email
    public Email() {
        this.smtp = smtp;
        prop.setProperty("mail.smtp.host", smtpServer);
        prop.put("mail.smtp.port", "25");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.auth", true);
    }

    public void sendEamil(String content) {
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Test email");

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(content, "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
