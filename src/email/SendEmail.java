package email;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class SendEmail {

    public static void SendEmail(String to,int bookingType) {

        //From
        String from = "AalborgZoo";
        String subject;
        String body;

        //Email
        final String username = "ds305e18@gmail.com";
        final String password = "hummermike";

        if (bookingType == 1) {
            subject = lectorMail.subject();
            body = lectorMail.body();
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getDefaultInstance(props, null);

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject("Testing Subject");
            message.setContent("Dear Mail Crawler,"
                    + "\n\n <h1> html+ </h1> No spam to my email, please!","text/html");

            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com",username,password);
            transport.sendMessage(message,message.getAllRecipients());
            transport.close();

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


}
