package helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fb.image;
import main.Request;
import instagram.Instagram;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
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

        HashMap<String, Double> results = new HashMap<String,Double>();
        String classify_endpoint = "https://gateway-a.watsonplatform.net/visual-recognition/api/v3/classify";
        String parameters = "api_key=" + api_key + "&url=" + link + "&version=2016-05-20";

        String data = new Request("GET", classify_endpoint, parameters, new String[]{}).getData();

        JsonParser parser = new JsonParser();
        JsonObject server_answer = (JsonObject) parser.parse(data);
        JsonArray tags = server_answer.getAsJsonArray("images").get(0).getAsJsonObject().getAsJsonArray("classifiers").get(0).getAsJsonObject().getAsJsonArray("classes");

        for(JsonElement e : tags){
        	if(!e.getAsJsonObject().get("class").getAsString().contains("color")){
        		results.put(e.getAsJsonObject().get("class").getAsString(), e.getAsJsonObject().get("score").getAsDouble());
        	} 
        }

        return results;
    }
    
    
    public static HashMap<String, Element> getAllTags(HashMap<String, String> images) throws IOException{
    	
    	//Nombre total d'images
    	int number_of_pictures = images.keySet().size();
    	//Résultat à retourner
    	HashMap<String, Element> result = new HashMap<String, Element>();
    	
    	
    	for(String link : images.keySet()){
    		
    		HashMap<String, Double> image_tags = getImageTags(link);
    		
    		for(String key : image_tags.keySet()){
    			
    			if(result.containsKey(key)){
    				result.get(key).setSentiment(result.get(key).sentiment + image_tags.get(key));
    				Date new_date = convertDate(images.get(link));
    				if(new_date.after(result.get(key).date)){
    					result.get(key).setDate(new_date);
    				}
    				
    			}
    			else{
    				result.put(key, new Element(image_tags.get(key), convertDate(images.get(link))));
    			}
    		}
    	}
    	

    	Double max_score = 0.0;
    	
    	for(String key : result.keySet()){
    		
    		Double weighted_score = Math.floor(result.get(key).sentiment/number_of_pictures * 100000) / 100000;
    		result.get(key).setSentiment(weighted_score);
    		max_score = Math.max(max_score, weighted_score);
    	}
    	
    	extendScores(result, max_score);
    	
    	return result;
    }
    
    
    
    public static void extendScores(HashMap<String, Element> list, Double max_score){
    	
    	for(String key : list.keySet()){
    		list.get(key).setSentiment(list.get(key).sentiment/max_score);
    	}
    	
    }
    
    
    public static Date convertDate(String date){
    	long ddd = Long.parseLong(date)*1000;
    	Date d = new Date(ddd);
		return d;
    }
    
    public static void main(String[] args) throws IOException, JSONException, ParseException {    	
    	
    	Instagram insta = new Instagram();
		HashMap<String, String> images = insta.getUserImages(insta.access_token);
		HashMap<String, Element> result = getAllTags(images);
	}

}
