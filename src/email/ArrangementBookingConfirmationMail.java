package email;

import bookings.ArrangementBooking;

public class ArrangementBookingConfirmationMail {

    public static String subject() {

        String subject;

        subject = "Børnfødselsdag";

        return subject;
    }

    public static String body(ArrangementBooking arrangementBooking) {

        String body;

        body = "<div style=\"margin:0; padding:4%;\">" +
                "<div class=\"head\" style=\"width: 100%; padding-bottom: 40px; overflow: hidden;\">" +
                "\t<div class=\"afsender\" style=\"float:left; width:40%;\">" +
                "Hej " + arrangementBooking.getCustomer().getContactPerson() +
                "</div>" +
                "<div class=\"logo\" style=\"float:right; height: 75px; width:40%;\">" +
                //"<img src=\"cid:Zoo\" alt=\"Aalborg Zoo\" align=\"right\">" +
                "</div>" +
                "</div>" +
                "" +
                "Vi bekræfter hermed, at der kommer " + arrangementBooking.getParticipants() + " til fødselsdag>\n" +
                "\t<p style=\"padding-left: 4em;\">Dato: " + arrangementBooking.getDateTime().toLocalDate() + ". Kl. " + arrangementBooking.getDateTime().toLocalTime() + "</p>\n" +
                "\n" +
                "I bedes medbringe dette brev til Zoo\'s billetsalg," +
                "Vi glæder os til at se jer i Aalborg Zoo til spændende og lærerige oplevelser.<br>\n" +
                "<br>\n" +
                "Venlig hilsen<br>\n" +
                "Aalborg Zoo<br>\n" +
                "\n" +
                "</div>'";

        return body;
    }


}
