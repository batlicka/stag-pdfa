package org.api;

import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.ArrayList;
import java.util.Properties;

public class Email {
    //*** odstranit komentaře s heslama
    private Properties prop = System.getProperties();
    private String user;
    private String pass;
    private String from;
    private String to;
    private String host;
    private String port;
    private Boolean authentization;
    private Session session;

    //https://stackoverflow.com/questions/46663/how-can-i-send-an-email-by-java-application-using-gmail-yahoo-or-hotmail
    //for gmail is needfull allow access for less secured applications: https://support.google.com/accounts/answer/6010255#zippy=%2Ckdy%C5%BE-je-v-%C3%BA%C4%8Dtu-zapnut%C3%BD-p%C5%99%C3%ADstup-pro-m%C3%A9n%C4%9B-zabezpe%C4%8Den%C3%A9-aplikace
    public Email(ArrayList<String> emailProperties) {
        //***
        this.user = emailProperties.get(0);
        this.pass = emailProperties.get(1);
        this.from = emailProperties.get(2);
        this.to = emailProperties.get(3);
        this.host = emailProperties.get(4);
        this.port = emailProperties.get(5);
        this.authentization = Boolean.parseBoolean(emailProperties.get(6));

        prop.put("mail.smtp.user", from);
        prop.put("mail.smtp.port", port);
        prop.put("mail.smtp.host", host);
    }

    //*** sendEamil
    public void sendEamil(String content) {
        //Session session = Session.getDefaultInstance(prop);
        if ((authentization)) {
            prop.put("mail.smtp.auth", true);
            prop.put("mail.smtp.password", pass);
            prop.put("mail.smtp.starttls.enable", "true");
            session = Session.getInstance(prop,
                    new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(user, pass);
                        }
                    });
        } else {
            prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            session = Session.getDefaultInstance(prop);
        }

        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Report from stag-pdfa");
            message.setText(content);

            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

        } catch (MessagingException e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
        }
    }
}
