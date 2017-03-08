package helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fb.image;
import instagram.Instagram;
import main.Request;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

/**
 * Created by hannamegaouel on 06/03/2017.
 */
public class ImageFunctions {

    private static String api_key = "ed9d3c4315dc0f80ede5e87d550e9816529b211d";

    //Input: link to the desired image
    //Output: hashmap of weighted tags
    public static HashMap<String,Double> getImageTags(String link) throws IOException {

        HashMap<String,Double> results = new HashMap<String,Double>();
        String classify_endpoint = "https://gateway-a.watsonplatform.net/visual-recognition/api/v3/classify";
        String parameters = "api_key=" + api_key + "&url=" + link + "&version=2016-05-20";

        String data = new Request("GET", classify_endpoint, parameters, new String[]{}).getData();

        JsonParser parser = new JsonParser();
        JsonObject server_answer = (JsonObject) parser.parse(data);
        JsonArray tags = server_answer.getAsJsonArray("images").get(0).getAsJsonObject().getAsJsonArray("classifiers").get(0).getAsJsonObject().getAsJsonArray("classes");

        for(JsonElement e : tags){
            results.put(e.getAsJsonObject().get("class").getAsString(), e.getAsJsonObject().get("score").getAsDouble());
        }

        return results;
    }
	public static void main(String[] args) throws IOException, JSONException, ParseException {
		Instagram i = new Instagram();
		List<String> links = i.getUserImages(Instagram.access_token);
		System.out.println(getImageTags(links.get(0)));
	}

}
