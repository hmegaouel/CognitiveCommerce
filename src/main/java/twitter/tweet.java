package twitter;

import analysis.functions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class tweet {
	
	private String twitterUser;
	private ArrayList<String> hastags = new ArrayList<String>();
	private ArrayList<String> arrotags = new ArrayList<String>();
	public HashMap<String,Element> result = new HashMap<String,Element>();
	private int w = 0;
	public HashMap<String,Element> positive = new HashMap<String,Element>();
	public HashMap<String,Element> negative = new HashMap<String,Element>();
	private final Date currentDate;
	
	
	public tweet(String user, Date date) {
		this.twitterUser = user;
		this.currentDate = date;
	}
	
	public void getTweets(String twitURL) throws IOException, JSONException, ParseException {
		System.out.println("start");
		if (w<1) {
			if (twitURL == null) {
				String okSearch = "from:"+twitterUser+"&size=100";
				twitURL = "https://cdeservice.eu-gb.mybluemix.net/api/v1/messages/search?q="+okSearch;
			}
			new download();
			String data = download.downloadURL(twitURL);
			JSONArray tweets = new JSONObject(data).getJSONArray("tweets");
			print("Working on "+w*100+"-"+((w+1)*100)+" tweets");
			w = w+1;
			for (int i=0; i<tweets.length(); i++) {
				JSONObject msg = tweets.getJSONObject(i).getJSONObject("message");
				String body = msg.getString("body");
				JSONArray hashtags = msg.getJSONObject("twitter_entities").getJSONArray("hashtags"); //.text
				JSONArray arrobasetags = msg.getJSONObject("twitter_entities").getJSONArray("user_mentions"); //.screen_name
				body = removeHashtags(hashtags,body);
				body = removeArrobase(arrobasetags,body);
				JSONArray keywords = functions.getKeywords(body);
				String datem = msg.getString("postedTime");
				Date dd = main.date.twitterinputFormat.parse(datem);
				HashMap<String,Double> sentiments = functions.getSentiment(keywords,body);
				//print(body);
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
			int results = new JSONObject(data).getJSONObject("search").getInt("results");
			int current = new JSONObject(data).getJSONObject("search").getInt("current");
			if (current<results) {
				String url =
						new JSONObject(data).getJSONObject("related")
						.getJSONObject("next").getString("href");
				getTweets(url);
			}
		}
		
	}

	private String removeHashtags(JSONArray hashtags, String body) throws JSONException {
		for (int j=0; j<hashtags.length();j++) {
			String hashtg = hashtags.getJSONObject(j).getString("text");
			hastags.add(hashtg);
			String regex = "#[A-Za-z]+";
			body = body.replaceAll(regex, "");
		}
		return body;
	}
	
	private String removeArrobase(JSONArray arrobasetags, String body) throws JSONException {
		for (int j=0; j<arrobasetags.length();j++) {
			String arro = arrobasetags.getJSONObject(j).getString("screen_name");
			arrotags.add(arro);
			String regex = "@[A-Za-z]+";
			body = body.replaceAll(regex, "");
		}
		return body;
	}
	
	public static void print(String msg) {
		System.out.println(msg);
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
	
	public JSONObject getEvent(List<Events> events){
		return functions.getBestEvent(events, result, currentDate);
	}

}
