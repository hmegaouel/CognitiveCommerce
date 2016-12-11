package fb;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter.Element;
import twitter.Events;

import analysis.functions;

public class Facebook {
	
	private int i = 0;
	private HashMap<String, Element> positive;
	private HashMap<String, Element> negative;
	private final Date currentDate;
	
	private HashMap<String,Element> result = new HashMap<String,Element>();
	
	public Facebook(Date dt) {
		this.currentDate = dt;
	}
	
	public void show() {
		for (String key : result.keySet()) {
			Element old = result.get(key);
			functions.print(key+" : "+old.sentiment+" ("+old.date+")");
		}
	}
	
	public void processPosts(String url) throws IOException, JSONException, ParseException {
		
		functions.print("Getting page "+i);
		i++;
		
		String posts = null;
		if (url == null) {
			posts = download.downloadURL(new String[] {"posts{message,created_time}"}, null);
		} else {
			posts = download.downloadURL(null, url);
		}
		functions.print(posts);
		JSONObject fbObj = new JSONObject(posts);
		if (url == null) fbObj = fbObj.getJSONObject("posts");
		JSONArray postsObject = fbObj.getJSONArray("data");
		for (int i=0;i<postsObject.length();i++){
			if (postsObject.getJSONObject(i).has("message")){
				analyseMessage(postsObject);
			}
		}
		if (fbObj.getJSONObject("paging").has("next") && i<3) {
			processPosts(fbObj.getJSONObject("paging").getString("next"));
		} else {
			processResults();
		}

	}
	
	public void analyseMessage(JSONArray postsObject) throws JSONException, IOException, ParseException {
		String body = postsObject.getJSONObject(i).getString("message");
		JSONArray keywords = functions.getKeywords(body);
		//System.out.println(keywords.toString());
		HashMap<String,Double> sentiments = functions.getSentiment(keywords,body);
		String datem = postsObject.getJSONObject(i).getString("created_time");
		Date dd = main.date.inputFormat.parse(datem);
		for (String key : sentiments.keySet()) {
			if (result.containsKey(key)){
				Element old = result.get(key);
				old.setSentiment(old.sentiment+sentiments.get(key));
				result.put(key, old);
			} else {
				Element entry = new Element(sentiments.get(key),dd);
				result.put(key, entry);
			}
		}
	}
	
	public void processResults(){
		List<HashMap<String, Element>> resultats = functions.positivenegative(result);
		positive = resultats.get(0);
		negative = resultats.get(1);
		for (String key : positive.keySet()) {
			functions.print(key+" : "+positive.get(key));
		}
		for (String key : negative.keySet()) {
			functions.print(key+" : "+negative.get(key));
		}
	}
	
	public String getEvent(List<Events> events){
		return functions.getBestEvent(events, positive, negative, currentDate);
	}

}
