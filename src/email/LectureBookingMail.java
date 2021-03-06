package email;


import bookings.LectureBooking;
import customers.LectureBookingCustomer;

class LectureBookingMail {

    static String subject() {

        String subject;

        subject = "Skoletjeneste Aalborg Zoo";

        return subject;
    }

    static String body(LectureBooking lectureBooking) {
        LectureBookingCustomer lectureBookingCustomer = (LectureBookingCustomer) lectureBooking.getCustomer();
        String body;

        body = "<div style=\"margin:0; padding:4%;\">" +
                "<div class=\"head\" style=\"width: 100%; padding-bottom: 40px; overflow: hidden;\">" +
                "\t<div class=\"afsender\" style=\"float:left; width:40%;\">" +
                lectureBookingCustomer.getSchoolName() + "<br>" +
                lectureBooking.getCustomer().getContactPerson() +
                "</div>" +
                "<div class=\"logo\" style=\"float:right; height: 75px; width:40%;\">" +
                //"<img src=\"cid:Zoo\" alt=\"Aalborg Zoo\" align=\"right\">" +
                "</div>" +
                "</div>" +
                "" +
                "Vi bekræfter hermed, at " + lectureBooking.getParticipants() + " elever og " + lectureBooking.getNoOfTeachers() + " lærere kommer til undervisning i Aalborg Zoo\'s skoletjeneste<br>\n" +
                "\t<p style=\"padding-left: 4em;\">Dato: " + lectureBooking.getDateTime().toLocalDate() + ". Kl. " + lectureBooking.getDateTime().toLocalTime() + "</p>\n" +
                "\t<p style=\"padding-left: 4em;\">For at høre om: " + lectureBooking.getChoiceOfTopic().toString() + "</p>\n" +
                "\n" +
                "I bedes medbringe dette brev til Zoo\'s billetsalg, da der herefter betales undervisningspris på 40 kr. pr\n" +
                "person for entré og undervisning. Der betales både for elever og lærere. Skoleforvaltningen i Aalborg\n" +
                "Kommune afholder alle udgifter ved besøget. Fra Zoos billetsalg går I til skoletjenestens lokaler hvor jeres\n" +
                "underviser vil tage imod jer.<br>\n" +
                "\n" +
                "Max. 1 voksen til skolepris pr 8 børn. Øvrige voksne betaler fuld pris. Denne ordning kan ikke kombineres\n" +
                "med andre rabatordninger. <br>\n" +
                "<br>\n" +
                "Vi glæder os til at se jer i Aalborg Zoo til spændende og lærerige oplevelser.<br>\n" +
                "<br>\n" +
                "Venlig hilsen<br>\n" +
                "Aalborg Zoo<br>\n" +
                "\n" +
                "</div>'";

        return body;
    }

    static String rejectBody(LectureBooking lectureBooking){
        LectureBookingCustomer lectureBookingCustomer = (LectureBookingCustomer) lectureBooking.getCustomer();
        String body;

        body = "<div style=\"margin:0; padding:4%;\">" +
                "<div class=\"head\" style=\"width: 100%; padding-bottom: 40px; overflow: hidden;\">" +
                "\t<div class=\"afsender\" style=\"float:left; width:40%;\">" +
                lectureBookingCustomer.getSchoolName() + "<br>" +
                lectureBooking.getCustomer().getContactPerson() +
                "</div>" +
                "<div class=\"logo\" style=\"float:right; height: 75px; width:40%;\">" +
                //"<img src=\"cid:Zoo\" alt=\"Aalborg Zoo\" align=\"right\">" +
                "</div>" +
                "</div>" +
                "" +
                "Vi har desværre ikke mulighed for at afholde undervisning i Aalborg Zoo\'s skoletjeneste den: " + lectureBooking.getDateTime().toLocalDate() + ". kl. " + lectureBooking.getDateTime().toLocalTime() + "</p>\n" +
                "Hvis der er spørgsmål tag kontakt til os på telefon: " + "99 99 99 99" + " eller skriv en mail til zoo@mail.dk" +
                "Vi håber at se jer på andet tidspunkt i Aalborg Zoo til spændende og lærerige oplevelser.<br>\n" +
                "<br>\n" +
                "Venlig hilsen<br>\n" +
                "Aalborg Zoo<br>\n" +
                "\n" +
                "</div>'";

        return body;
    }

}
