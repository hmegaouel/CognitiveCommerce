package helpers;

import main.Request;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;


public class TextFunctions {

    private static String alchemyAPIKey = "4a2e14d80c21f4334269db52847c5ca57bab7448";

    public static JSONArray getKeywords(String body) throws IOException, JSONException {
        if ((body != null) && (body != "")) {
            String parameters = "apikey="+alchemyAPIKey+"&outputMode=json&text="+ URLEncoder.encode(body, "UTF-8");
            String url = "https://gateway-a.watsonplatform.net/calls/text/TextGetRankedKeywords";
            String answer = new Request("GET", url, parameters, new String[]{}).getData();
            JSONObject ans = new JSONObject(answer);
            System.out.println(ans);
            if (ans.has("keywords")) {
                JSONArray keywords = ans.getJSONArray("keywords");
                System.out.println("Done getting keywords, length: "+keywords.length());
                return keywords;
            }
        }
        System.out.println("no keywords");
        return new JSONArray();
    }

    public static HashMap<String,Double> getSentiment(JSONArray keywords, String body) throws IOException, JSONException {
        HashMap<String,Double> sentiments = new HashMap<String,Double>();
        if (keywords == null || keywords.length() == 0) {
            System.out.println("NO KEYWORDS");
            return sentiments;
        }
        String word = "";
        for (int i=0;i<keywords.length();i++) {
            if (keywords.getJSONObject(i).has("text")) {
                word += keywords.getJSONObject(i).getString("text").replaceAll("[^A-Za-z0-9 ]", "")+"|";
            }
        }
        String parameters = "apikey="+alchemyAPIKey+"&outputMode=json&text="+ URLEncoder.encode(body, "UTF-8")+"&targets="+word;
        String url = "https://gateway-a.watsonplatform.net/calls/text/TextGetTargetedSentiment";
        String answer = new Request("GET", url, parameters, new String[]{}).getData();
        JSONObject ansObj = new JSONObject(answer);
        if (ansObj.has("statusInfo")) {
            System.out.println("ERROR IN SENTIMENT");
            return sentiments;
        }
        if (ansObj.has("results")) {
            JSONArray sents = new JSONObject(answer).getJSONArray("results");
            for (int i=0;i<sents.length();i++) {
                double score = 0;
                if (sents.getJSONObject(i).getJSONObject("sentiment").has("score")) {
                    score = sents.getJSONObject(i).getJSONObject("sentiment").getDouble("score");
                }
                sentiments.put(sents.getJSONObject(i).getString("text"), score);
            }
        }
        return sentiments;
    }

}
