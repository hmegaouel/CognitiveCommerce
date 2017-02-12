package main;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hannamegaouel on 12/02/2017.
 */
public class RecupMeteo {

    private static String username = "6bb66eeb-b35b-4e08-99c9-9f9513134bbc";
    private static String password = "IEJXiwtRbl";
    private static RecupCoordGPS gps = new RecupCoordGPS();

    private static String downloadURL(String urlString) throws IOException {

        //tweet.print("Download from "+urlString);

        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(urlString);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            String userpass = username+":"+password;
            String basicAuth = "Basic " + new String(Base64.encodeBase64(userpass.getBytes()));
            urlConnection.setRequestProperty("Authorization", basicAuth);
            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream, "UTF-8"));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            System.out.println("Exception while downloading url"+ e.toString());
            return "";
        }finally{
            iStream.close();
            urlConnection.disconnect();
            return data;
        }
    }

    public String getMeteo() throws IOException {
        double[] coords = gps.GPSSetter();
        String url = "https://twcservice.mybluemix.net/api/weather/v1/geocode/"+coords[0]+"/"+coords[1]+"/forecast/daily/3day.json";
        String weatherData = downloadURL(url);
        JSONObject json = new JSONObject(weatherData);
        JSONObject today = json.getJSONArray("forecasts").getJSONObject(0);
        String weatherType = today.getJSONObject("day").getString("golf_category");
        System.out.println(weatherType);
        return weatherType;
    }

    public static void main(String[] args) throws IOException {
        RecupMeteo meteo = new RecupMeteo();
        meteo.getMeteo();
    }


}
