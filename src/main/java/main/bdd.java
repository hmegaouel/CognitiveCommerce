package main;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import twitter.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hannamegaouel on 12/01/2017.
 */
public class bdd {

    private String key = "isermaravelturpealldomea"; //iontrandeadegralinedleel
    private String pass = "8ace1f814b56e141a107e9883abac86dc798fd6f"; //02ca06c41949d761c1f08364bd15d15b7774cf7c
    private String account = "3f53d18a-bd59-463c-9eca-5c37a5d02e5a-bluemix";
    private CloudantClient client;
    private static Database db;
    private static double lat;
    private static double lon;

    public bdd(double lat, double lon) {

        this.lat = lat;
        this.lon = lon;
        System.out.println("Connecting to bdd");
        client = ClientBuilder.account(account)
                .username(key)
                .password(pass)
                .build();
        db = client.database("events", false);
        this.checkConnection();
    }

    public void checkConnection() {

        System.out.println("Server Version: " + client.serverVersion());
        System.out.println(db.info());

    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

    public static List<Events> searchByCity() throws IOException {

        List<JsonObject> allFoos = db.getAllDocsRequestBuilder().includeDocs(true).build()
                .getResponse().getDocsAs(JsonObject.class);
        List<Events> events = new ArrayList<Events>();
        for(JsonObject o : allFoos){
            if (o.has("latlon") && o.has("city")) {
                JsonElement latlon = o.get("latlon");
                JsonElement city = o.get("city");
                String[] latlonString = latlon.toString().replace("[","").replace("]","").split(",");
                if (latlonString.length == 2) {
                    double elat = Double.parseDouble(latlonString[0]);
                    double elon = Double.parseDouble(latlonString[1]);
                    double score = distance(lat, elat, lon, elon);
                    if (score < 20000) {
                        JsonElement tags = o.get("tags");
                        if (tags == null) {
                            //anull++;
                        } else {
                            //notnull++;
                            String[] tagsString = tags.toString().replace("\"","").split(",");
                            events.add(new Events(o,new ArrayList<String>(Arrays.asList(tagsString))));
                        }
                    }
                }
            }
        }
        return events;
    }

}
