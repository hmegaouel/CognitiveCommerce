package fb;

import analysis.functions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import twitter.Element;
import twitter.Events;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Facebook {
	
	private int i = 0;
	private HashMap<String, Element> positive;
	private HashMap<String, Element> negative;
	private final Date currentDate;
	private final String token;
	private download dwld;
	
	private HashMap<String,Element> result = new HashMap<String,Element>();
	
	public Facebook(Date dt, String token) {
		this.currentDate = dt;
		this.token = token;
		this.dwld = new download(token);
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
			posts = dwld.downloadURL(new String[] {"posts{message,created_time}"}, null);
		} else {
			posts = dwld.downloadURL(null, url);
		}
		functions.print(posts);
		JSONObject fbObj = new JSONObject(posts);
		if (url == null) fbObj = fbObj.getJSONObject("posts");
		JSONArray postsObject = fbObj.getJSONArray("data");
		for (int i=0;i<postsObject.length();i++){
			if (postsObject.getJSONObject(i).has("message")){
				analyseMessage(postsObject.getJSONObject(i));
			}
		}
		if (fbObj.getJSONObject("paging").has("next") && i<3) {
			processPosts(fbObj.getJSONObject("paging").getString("next"));
		} else {
			processResults();
		}

	}
	
	public void analyseMessage(JSONObject postsObject) throws JSONException, IOException, ParseException {
		String body = postsObject.getString("message");
		JSONArray keywords = functions.getKeywords(body);
		//System.out.println(keywords.toString());
		HashMap<String,Double> sentiments = functions.getSentiment(keywords,body);
		String datem = postsObject.getString("created_time");
		Date dd = main.date.fbinputFormat.parse(datem);
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
