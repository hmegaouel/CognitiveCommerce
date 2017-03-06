package main;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.client.ClientProperties;

import javax.swing.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hannamegaouel on 06/03/2017.
 */
public class Request {

    private String method;
    private String url;
    private String parameters;
    private String[] auth;

    public Request(String method, String url, String parameters, String[] auth) {

        this.method = method;
        this.url = url;
        this.parameters = parameters;
        this.auth = auth;

    }

    public String getData() throws IOException {
        if (method == "GET") {
            return getRequest();
        } else if (method == "POST") {
            return postRequest();
        } else {
            return "";
        }
    }

    public String getRequest() throws IOException {

        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        String finalUrl = url+"?"+parameters;

        try{
            // Connexion
            //String encodedURL=java.net.URLEncoder.encode(urlString,"UTF-8");
            URL url = new URL(finalUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            if (auth.length>0) {
                String userpass = auth[0]+":"+auth[1];
                String basicAuth = "Basic " + new String(Base64.encodeBase64(userpass.getBytes()));
                urlConnection.setRequestProperty("Authorization", basicAuth);
            }

            urlConnection.connect();

            // Lecture de la réponse
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream, "UTF-8"));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();
            br.close();
        }

        catch(Exception e){
            System.out.println("Exception while downloading url"+ e.toString());
        }

        finally{
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return data;

    }

    public String postRequest() {

        String json = null;
        MultivaluedMap<String, String> formdata = new MultivaluedHashMap<String, String>();

        Client client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, 10000); // si pas internet, apres un délai d'attente raisonnable : exception
        WebTarget webTarget = client.target(url);
        try {
            json = webTarget.request().post(Entity.form(formdata), String.class);
        } catch (Exception e) {
            System.out.println("Pas de connexion internet");
            JOptionPane.showMessageDialog(null,"Pas de connexion internet");
        }

        return json;

    }

}
