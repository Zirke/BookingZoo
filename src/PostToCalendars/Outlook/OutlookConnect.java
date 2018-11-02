package PostToCalendars.Outlook;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.schema.AppointmentSchema;
import microsoft.exchange.webservices.data.core.service.folder.CalendarFolder;
import microsoft.exchange.webservices.data.search.CalendarView;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.search.FolderView;
import microsoft.exchange.webservices.data.core.service.schema.FolderSchema;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;
import microsoft.exchange.webservices.data.core.enumeration.search.FolderTraversal;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.property.complex.Mailbox;
import microsoft.exchange.webservices.data.search.ItemView;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.property.definition.PropertyDefinition;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import java.net.URI;

public class OutlookConnect {


    public static void main(String[] args) {
        OutlookConnect connect = new OutlookConnect();
        connect.createAppointment();

    }

    public void createAppointment() {

        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);

        ExchangeCredentials credentials
                = new WebCredentials("aalborgzoo305@outlook.com",
                "ds305e18");
        service.setCredentials(credentials);

        try {
            service.setUrl(new URI("https://outlook.office365.com/EWS/Exchange.asmx"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        try {
            service.autodiscoverUrl("aalborgzoo305@outlook.com");
        } catch (Exception e) {
            e.printStackTrace();
        }

        FolderView fv = new FolderView(100);
        fv.setTraversal(FolderTraversal.Deep);


        try {
            Appointment appointment = new Appointment(service);
            appointment.setStart(new Date());
            appointment.setEnd(new Date());
            appointment.save();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map readAppointment(Appointment appointment) {
        Map appointmentData = new HashMap();
        try {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            appointmentData.put("appointmentItemId", appointment.getId().toString());
            appointmentData.put("appointmentSubject", appointment.getSubject());
            appointmentData.put("appointmentStartTime", df.format(appointment.getStart()));
            appointmentData.put("appointmentEndTime", df.format(appointment.getEnd()));
        } catch (ServiceLocalException e) {
            e.printStackTrace();
        }
        return appointmentData;
    }
}