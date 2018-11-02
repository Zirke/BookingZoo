package PostToCalendars.Outlook;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

public class PostToOutlook {

    Client client = ClientBuilder.newClient();

    Response response = client.target("https://outlook.office.com/api/v2.0/me/calendarview?startDateTime=2014-10-01T01:00:00&endDateTime=2014-10-31T23:00:00&$select=Subject").request().get();


}