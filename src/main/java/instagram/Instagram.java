package instagram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Instagram {
	
	// Accès basiques
	static String userinfo_endpoint = "https://api.instagram.com/v1/users/self/";
	static String latestmedia_endpoint = "https://api.instagram.com/v1/users/self/media/recent/";
	
	// Accès "spéciaux" (necessitent une validation de l'appli par Instagram)
	static String recentlikes_endpoint = "https://api.instagram.com/v1/users/self/media/liked";
	static String followed_endpoint = "https://api.instagram.com/v1/users/self/follows";
	
	// Token utilisateur test
	public static String access_token = "4739239538.250ea0b.f33538c037b3425ca31f44d79b0a0dc6";
	
	
	
	public List<String> getUserImages(String token) throws IOException{
		String response = downloadURL(latestmedia_endpoint, token);
		System.out.println(response);
		List<String> result = getImageList(response);
		return result;
	}
	
	
	private List<String> getImageList(String response){
		List<String> result = new ArrayList<String>();
		
		// Parsing des résultats
        JsonParser parser = new JsonParser();
        JsonObject server_answer = (JsonObject) parser.parse(response);
        JsonArray media_array = server_answer.getAsJsonArray("data");
        for(JsonElement o : media_array){
        	JsonObject obj = o.getAsJsonObject();
        	String link = obj.getAsJsonObject("images").getAsJsonObject("standard_resolution").get("url").getAsString();
        	result.add(link);
        }
		return result;
	}
	
	// Faire une fonction plus générique ?
	public String downloadURL(String urlString, String token) throws IOException{
		
		String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        
        try{
        	// Connexion
            URL url = new URL(urlString + "?access_token=" + token);
            urlConnection = (HttpURLConnection) url.openConnection();
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
        	iStream.close();
        	urlConnection.disconnect();
        }
        
    return data;
	}
	
}
