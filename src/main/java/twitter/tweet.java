package twitter;

import helpers.Element;
import helpers.Events;
import main.Request;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static helpers.EventFunctions.getBestEvent;
import static helpers.TextFunctions.getKeywords;
import static helpers.TextFunctions.getSentiment;

public class tweet {
	
	private String twitterUser;
	private ArrayList<String> hastags = new ArrayList<String>();
	private ArrayList<String> arrotags = new ArrayList<String>();
	public HashMap<String, Element> result = new HashMap<String,Element>();
	private int w = 0;
	public HashMap<String,Element> positive = new HashMap<String,Element>();
	public HashMap<String,Element> negative = new HashMap<String,Element>();
	private final Date currentDate;

	private static String bluemixTwitterUsername = "82c55037-92c8-421f-8d4c-9c9e56b68809";
	private static String bluemixTwitterPass = "e6TaVDzihc";
	
	
	public tweet(String user, Date date) {
		this.twitterUser = user;
		this.currentDate = date;
	}
	
	public void getTweets(String twitURL) throws IOException, JSONException, ParseException {
		//System.out.println("start");
		if (w<1) {
			String searchParameter = "";
			if (twitURL == null) {
				String okSearch = "from:"+twitterUser+"&size=100";
				String okSearchEncoded=java.net.URLEncoder.encode(okSearch,"UTF-8");
				searchParameter = "q="+okSearchEncoded;
				twitURL = "https://cdeservice.eu-gb.mybluemix.net/api/v1/messages/search";
			}
			String data = new Request("GET", twitURL, searchParameter, new String[] {bluemixTwitterUsername,bluemixTwitterPass} ).getRequest();
			System.out.println(data);
			JSONArray tweets = new JSONObject(data).getJSONArray("tweets");
			print("Working on "+w*100+"-"+((w+1)*100)+" tweets");
			w = w+1;
			print("Number of tweets: "+tweets.length());
			for (int i=0; i<tweets.length(); i++) {
				JSONObject msg = tweets.getJSONObject(i).getJSONObject("message");
				System.out.println(msg);
				if (msg.has("body") && msg.has("twitter_entities") && msg.has("postedTime")) {
					if (msg.getJSONObject("twitter_entities").has("hashtags") && msg.getJSONObject("twitter_entities").has("user_mentions")) {
						String body = msg.getString("body");
						System.out.println("main getting hashtags");
						JSONArray hashtags = msg.getJSONObject("twitter_entities").getJSONArray("hashtags"); //.text
						System.out.println("main getting @");
						JSONArray arrobasetags = msg.getJSONObject("twitter_entities").getJSONArray("user_mentions"); //.screen_name
						System.out.println("main removing hashtags");
						body = removeHashtags(hashtags,body);
						System.out.println("main removing @");
						body = removeArrobase(arrobasetags,body);
						System.out.println("main getting keywords");
						JSONArray keywords = getKeywords(body);
						System.out.println("main getting time");
						String datem = msg.getString("postedTime");
						Date dd = main.date.twitterinputFormat.parse(datem);
						if (keywords.length()>0) {
							System.out.println("main getting sentiment");
							HashMap<String,Double> sentiments = getSentiment(keywords,body);
							System.out.println("main setting result");
							for (String key : sentiments.keySet()) {
								if (result.containsKey(key)){
									System.out.println("main updating key");
									Element old = result.get(key);
									old.setSentiment(old.sentiment+sentiments.get(key));
									result.put(key, old);
								} else {
									System.out.println("main new key");
									Element entry = new Element(sentiments.get(key),dd);
									result.put(key, entry);
								}
							}
						}
					}
				}
			}
			JSONObject dataObj = new JSONObject(data);
			if (dataObj.has("search")) {
				JSONObject searchObj = dataObj.getJSONObject("search");
				if (searchObj.has("results") && searchObj.has("current")) {
					int results = searchObj.getInt("results");
					int current = searchObj.getInt("current");
					if (current<results) {
						String url =
								new JSONObject(data).getJSONObject("related")
										.getJSONObject("next").getString("href");
						getTweets(url);
					}
				}
			}
		}
		
	}

	private String removeHashtags(JSONArray hashtags, String body) throws JSONException {
		if (body == null) {
			return "";
		}
		for (int j=0; j<hashtags.length();j++) {
			String hashtg = hashtags.getJSONObject(j).getString("text");
			hastags.add(hashtg);
			String regex = "#[A-Za-z]+";
			body = body.replaceAll(regex, "");
		}
		return body;
	}
	
	private String removeArrobase(JSONArray arrobasetags, String body) throws JSONException {
		if (body == null) {
			return "";
		}
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

}
