package email;

import bookings.ArrangementBooking;
import bookings.LectureBooking;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendEmail {

    public static void SendEmail(LectureBooking lectureBooking) {
        String subject = LectureBookingConformationMail.subject();
        String body = LectureBookingConformationMail.body(lectureBooking);
        String to = lectureBooking.getCustomer().getEmail();

        Setup(subject, body, to);
    }

    public static void SendEmail(ArrangementBooking arrangementBooking) {
        String subject = ArrangementBookingConfirmationMail.subject();
        String body = ArrangementBookingConfirmationMail.body(arrangementBooking);
        String to = arrangementBooking.getCustomer().getEmail();

        Setup(subject, body, to);
    }

    private static void Setup(String subject, String body, String to) {

        //From
        String from = "AalborgZoo";

        //Email
        final String username = "ds305e18@gmail.com";
        final String password = "hummermike";

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
            message.setSubject(subject);
            message.setContent(body, "text/html");

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
