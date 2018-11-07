package PostToCalendars.Outlook;

import javax.json.JsonArray;
import javax.print.attribute.standard.Media;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.IOException;
import java.net.*;

public class PostToOutlook {

    private static String authorizeEndPoint = ("https://login.microsoftonline.com/common/oauth2/v2.0/authorize?" +
            "client_id=f5bdb6a1-cdb8-4f11-8738-1b3655edf8f8" +
            "&response_type=code" +
            "&redirect_uri=https://login.microsoftonline.com/common/oauth2/nativeclient" +
            "&response_mode=query" +
            "&scope=offline_access%20user.read%20Calendars.ReadWrite" +
            "&state=305");


    public static void main(String[] args) {

        PostToOutlook.getPermission();

    }



    public static void getPermission(){

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(authorizeEndPoint);

        String response = target.request(MediaType.TEXT_PLAIN).get(String.class);

        System.out.println(response);


    }



}
