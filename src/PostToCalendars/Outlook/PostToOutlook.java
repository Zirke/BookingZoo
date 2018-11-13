package PostToCalendars.Outlook;

import org.glassfish.jersey.client.oauth2.ClientIdentifier;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;
import org.glassfish.jersey.client.oauth2.TokenResult;

import javax.json.JsonArray;
import javax.net.ssl.SSLPermission;
import javax.print.attribute.standard.Media;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.Console;
import java.io.IOException;
import java.net.*;
import java.util.Properties;
import java.util.Scanner;

public class PostToOutlook{

    private static final String authorizeEndPoint = "https://login.microsoftonline.com/common/oauth2/v2.0/authorize?";

    private static final String scope = (
            "client_id=f5bdb6a1-cdb8-4f11-8738-1b3655edf8f8" +
            "&response_type=code" +
            "&redirect_uri=https://login.microsoftonline.com/common/oauth2/nativeclient" +
            "&response_mode=query" +
            "&scope=offline_access%20user.read%20Calendars.ReadWrite" +
            "&state=305");

    private static final String accessTokenUri = "https://login.microsoftonline.com/common/oauth2/v2.0/token";

    public static void main(String[] args) {

        PostToOutlook.getPermissionReal();

    }



    public static void getPermission(){

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(authorizeEndPoint);


        ClientIdentifier clientId = new ClientIdentifier("f5bdb6a1-cdb8-4f11-8738-1b3655edf8f8", "huRAC18^]#$uolfpZHMG959");

        OAuth2CodeGrantFlow.Builder builder = OAuth2ClientSupport.authorizationCodeGrantFlowBuilder(clientId,
                authorizeEndPoint,
                accessTokenUri);


        OAuth2CodeGrantFlow flow = builder
                .property(OAuth2CodeGrantFlow.Phase.AUTHORIZATION, "read-only", "true")
                .scope(scope)
                .build();

        String statecodestring = flow.start();

        final TokenResult token = flow.finish("state", "code");


        System.out.println("Access Token: " + token.getAccessToken());

    }


    public static void getPermissionReal(){

        Client client = ClientBuilder.newClient();

        WebTarget target = client.target("https://login.live.com/oauth20_desktop.srf");

        String response = target.request().get(String.class);

        System.out.println(response);
    }
}

