package personality;

import com.ibm.watson.developer_cloud.personality_insights.v3.PersonalityInsights;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.Content;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.Profile;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.ProfileOptions;
import com.ibm.watson.developer_cloud.util.GsonSingleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import twitter.download;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

public class Insights {
    private Content content;
    private PersonalityInsights service = new PersonalityInsights("2016-10-20");
    private JSONObject twitterResults = new JSONObject();
    private String twitterUser;
    int w = 0;
    private HashMap<String,Double> scores = new HashMap<String, Double>();
    private Profile profile;

    /**
     * Instantiates a new Personality Insights service.
     */
    public Insights(String user) {
        this.twitterUser = user;
        service.setUsernameAndPassword("bcbe1553-1da4-4986-bb75-08df78320dc9","m1g2kvRtbcdM");
        twitterResults.put("contentItems", new JSONArray());
    }

    public void getTweets(String twitURL) throws IOException, JSONException, ParseException {
        System.out.println("Working on "+w*100+"-"+((w+1)*100)+" tweets");
        if (w<1) {
            if (twitURL == null) {
                String okSearch = "from:"+twitterUser+"&size=100";
                twitURL = "https://cdeservice.eu-gb.mybluemix.net/api/v1/messages/search?q="+okSearch;
            }
            new download();
            String data = download.downloadURL(twitURL);
            JSONArray tweets = new JSONObject(data).getJSONArray("tweets");
            w = w+1;
            for (int i=0; i<tweets.length(); i++) {
                JSONObject msg = tweets.getJSONObject(i).getJSONObject("message");
                String body = msg.getString("body");
                String datem = msg.getString("postedTime");
                Date dd = main.date.twitterinputFormat.parse(datem);
                long fin = dd.getTime();
                String id = msg.getString("id");
                String lang = msg.getString("twitter_lang");
                //System.out.println(body);
                addTweet(body,fin,id,lang);
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

    public void getJSON() {
        System.out.println(twitterResults);
    }

    public void addTweet(String body, Long dat, String id, String lang) {
        JSONObject tweetContent = new JSONObject();
        tweetContent.put("content", body);
        tweetContent.put("contenttype", "text/plain");
        tweetContent.put("created", dat);
        tweetContent.put("id", id);
        tweetContent.put("language", lang);
        tweetContent.put("sourceid", "Twitter API");
        tweetContent.put("userid", twitterUser);
        twitterResults.getJSONArray("contentItems").put(tweetContent);
    }

    public void buildFronJSON() throws FileNotFoundException{
        //System.out.println("Getting JSON");
        content = GsonSingleton.getGson().fromJson(twitterResults.toString(), Content.class);
    }

    public void getProfile() {
        ProfileOptions options = new ProfileOptions.Builder()
                .contentItems(content.getContentItems())
                .consumptionPreferences(true)
                .rawScores(true)
                .build();
        profile = service.getProfile(options).execute();
    }

    public void setScores() {
        JSONObject outputJSON = new JSONObject(profile.toString());
        //System.out.println(outputJSON);
        //System.out.println("Setting scores");
        JSONArray personality = outputJSON.getJSONArray("personality");
        JSONArray openness = personality.getJSONObject(0).getJSONArray("children");
        double art = openness.getJSONObject(1).getDouble("raw_score");
        double imagination = openness.getJSONObject(3).getDouble("raw_score");
        double intellect = openness.getJSONObject(4).getDouble("raw_score");
        JSONArray extraversion = personality.getJSONObject(2).getJSONArray("children");
        double activity = extraversion.getJSONObject(0).getDouble("raw_score");
        double excitement = extraversion.getJSONObject(3).getDouble("raw_score");
        double extroversion = personality.getJSONObject(2).getDouble("raw_score");
        double stress = personality.getJSONObject(4).getJSONArray("children").getJSONObject(1).getDouble("raw_score");
        scores.put("art",art);
        scores.put("imagination",imagination);
        scores.put("intellect",intellect);
        scores.put("activity",activity);
        scores.put("excitement",excitement);
        scores.put("extroversion",extroversion);
        scores.put("stress",stress);
    }

    public void showScores() {
        for (String key : scores.keySet()) {
            System.out.println(key+" : "+scores.get(key));
        }
    }

    public static void main(String [ ] args) throws FileNotFoundException, IOException, ParseException {
        Insights insight = new Insights("WongeneKIM");
        insight.getTweets(null);
        //insight.getJSON();
        insight.buildFronJSON();
        insight.getProfile();
        insight.setScores();
        insight.showScores();
    }

}