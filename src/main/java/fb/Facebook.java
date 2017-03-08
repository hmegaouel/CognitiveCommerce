package fb;

import main.Request;
import personality.res;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import helpers.Element;
import helpers.Events;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static helpers.EventFunctions.getBestEvent;
import static helpers.TextFunctions.getKeywords;
import static helpers.TextFunctions.getSentiment;

public class Facebook {
	
	private int i = 0;
	public HashMap<String, Element> positive;
	public HashMap<String, Element> negative;
	private final Date currentDate;
	private final String token;

	public HashMap<String,Element> result = new HashMap<String,Element>();
	
	public Facebook(Date dt, String token) {
		this.currentDate = dt;
		this.token = token;
	}
	
	public void processPosts(String url) throws IOException, JSONException, ParseException {

		System.out.println("Getting page "+i);
		i++;
		
		String posts = null;

		if (url == null) {
			String urlString ="https://graph.facebook.com/v2.8/me";
			String[] args = new String[] {"posts{message,created_time}"};
			String fields = strJoin(args,"%2C");
			String parameters = "access_token="+token+"&fields="+fields+"&format=json&method=get&pretty=0&suppress_http_code=1";
			posts = new Request("GET", urlString, parameters, new String[]{}).getData();
		} else {
			posts = new Request("GET", url, "", new String[]{}).getData();
		}
		System.out.println(posts);
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
		}

	}

	public static String strJoin(String[] aArr, String sSep) {
		StringBuilder sbStr = new StringBuilder();
		for (int i = 0, il = aArr.length; i < il; i++) {
			if (i > 0)
				sbStr.append(sSep);
			sbStr.append(aArr[i]);
		}
		return sbStr.toString();
	}
	
	public void analyseMessage(JSONObject postsObject) throws JSONException, IOException, ParseException {
		String body = postsObject.getString("message");
		JSONArray keywords = getKeywords(body);
		//System.out.println(keywords.toString());
		HashMap<String,Double> sentiments = getSentiment(keywords,body);
		String datem = postsObject.getString("created_time");
		Date dd = main.date.fbinputFormat.parse(datem);
		
		//Consumption Preferences affected to result
		res resultat=new res();
		HashMap<String,Element> h= res.getres(body,dd);
		result.putAll(h);
		//
		
		
		for (String key : sentiments.keySet()) {
			if (result.containsKey(key)){
				Element old = result.get(key);
				old.setSentiment(old.sentiment+sentiments.get(key));
				result.put(key, old);
				// res getres1
			} else {
				Element entry = new Element(sentiments.get(key),dd);
				result.put(key, entry);
				// res getres2
			}
		}
	}
	
	public JSONObject getEvent(List<Events> events) throws JSONException, FileNotFoundException {
		return getBestEvent(events, result, currentDate);
	}

}
