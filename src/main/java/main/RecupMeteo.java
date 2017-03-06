package main;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
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

    public String getMeteo() throws IOException, JSONException {
        double[] coords = gps.GPSSetter();
        String url = "https://twcservice.mybluemix.net/api/weather/v1/geocode/"+coords[0]+"/"+coords[1]+"/forecast/daily/3day.json";
        String weatherData = new Request("GET", url, "", new String[] {username, password}).getData();
        JSONObject json = new JSONObject(weatherData);
        JSONObject today = json.getJSONArray("forecasts").getJSONObject(0);
        String weatherType = "";
        if (today.has("day")) {
            weatherType = today.getJSONObject("day").getString("golf_category");
        } else {
            weatherType = today.getJSONObject("night").getString("golf_category");
        }
        System.out.println(weatherType);
        return weatherType;
    }

    public static void main(String[] args) throws IOException, JSONException {
        RecupMeteo meteo = new RecupMeteo();
        meteo.getMeteo();
    }


}
