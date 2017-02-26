package analysis;

import com.google.gson.JsonObject;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter.Element;
import twitter.Events;
import twitter.download;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;

public class functions {

    private static NormalizedLevenshtein l = new NormalizedLevenshtein();
    //private static Word2vec word2vec = new Word2vec("fr");

    public static JSONArray getKeywords(String body) throws IOException, JSONException {
        if ((body != null) && (body != "")) {
            String stuff = "outputMode=json&text="+ URLEncoder.encode(body, "UTF-8");
            String answer = download.alchemyPostRequest("https://gateway-a.watsonplatform.net/calls/text/TextGetRankedKeywords",stuff);
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
            print("NO KEYWORDS");
            return sentiments;
        }
        String word = "";
        for (int i=0;i<keywords.length();i++) {
            if (keywords.getJSONObject(i).has("text")) {
                word += keywords.getJSONObject(i).getString("text").replaceAll("[^A-Za-z0-9 ]", "")+"|";
            }
        }
        //print("About to do alchemypostrequest");
        //print(word);
        String stuff = "outputMode=json&text="+ URLEncoder.encode(body, "UTF-8")+"&targets="+word;
        //print(stuff);
        String answer = download.alchemyPostRequest("https://gateway-a.watsonplatform.net/calls/text/TextGetTargetedSentiment",stuff);
        System.out.println("alchemy sentiment answer:");
        print(answer);
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

    public static List<HashMap<String, Element>> positivenegative(HashMap<String,Element> result) {
        SortedSet<Element> values = new TreeSet<Element>(new SentimentComparator());
        for (Entry<String, Element> entry : result.entrySet()) {
            values.add((Element) entry.getValue());
        }
        HashMap<String,Element> positive = new HashMap<String,Element>();
        HashMap<String,Element> negative = new HashMap<String,Element>();
        for (int j=0;j<5;j++){
            Element lastpos = (Element) values.toArray()[values.toArray().length-1-j];
            String positiv = getKeyByValue(result,lastpos);
            Element firstpos = (Element) values.toArray()[j];
            String negativ = getKeyByValue(result,firstpos);
            positive.put(positiv, lastpos);
            negative.put(negativ, firstpos);
        }
        List<HashMap<String, Element>> myData = new ArrayList<HashMap<String, Element>>();
        myData.add(positive);
        myData.add(negative);
        return myData;
    }

    public static <T, E> String getKeyByValue(HashMap<String, Element> result2, Element object) {
        for (Entry<String, Element> entry : result2.entrySet()) {
            if (object == entry.getValue()) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static double getEventScore(Events event, HashMap<String, Element> allElements, Date currentDate) {
        double result = 0;
        double score;
        //System.out.println("Getting score");
        //System.out.println(allElements.size());
        for (int i=0;i<event.keywords.size();i++) {
            for (String key : allElements.keySet()) {
                //score = Word2vec.getScore(key,event.keywords.get(i));
                long diff = (1+currentDate.getTime()-allElements.get(key).date.getTime())/(1000*60*60*24);
                //result += (allElements.get(key).sentiment)*(score)/diff;
                result += (allElements.get(key).sentiment)*(1-l.distance(key, event.keywords.get(i)))/diff;
            }
        }
        return result;
    }

    static class eventToCompare {
        public JsonObject name;
        public double score;
        public eventToCompare(JsonObject name, double score) {
            this.name = name;
            this.score = score;
        }
    }

    static class MyJSONComparator implements Comparator<eventToCompare> {

        public int compare(eventToCompare o1, eventToCompare o2) {
            if (o1.score < o2.score) return 1;
            if (o1.score > o2.score) return -1;
            return 0;
        }

    }

    public static JSONObject getBestEvent(List<Events> events, HashMap<String, Element> allElements, Date currentDate) throws JSONException {
        JSONObject jo = new JSONObject();
        ArrayList<eventToCompare> list = new ArrayList<eventToCompare>();
        JsonObject best = events.get(0).name; double top = getEventScore(events.get(0),allElements,currentDate);
        //print(events.get(0).name+" : "+top);
        list.add(new eventToCompare(events.get(0).name, top));
        for (int i=1; i<events.size();i++) {
            //print("calculating score for "+events.get(i).name);
            double score = getEventScore(events.get(i),allElements,currentDate);
            list.add(new eventToCompare(events.get(i).name, score));
        }
        for (eventToCompare e : list) {
            System.out.print(e.score+";");
        }
        Collections.sort(list, new MyJSONComparator());
        /*System.out.println("**************");
        for (eventToCompare e : list) {
            System.out.print(e.score+";");
        }
        */
        jo.put(list.get(0).name.get("title").toString(), list.get(0).name);
        if (list.size()>1) {
            jo.put(list.get(1).name.get("title").toString(), list.get(1).name);
        }
        if (list.size()>2) {
            jo.put(list.get(2).name.get("title").toString(), list.get(2).name);
        }
        if (list.size()>3) {
            jo.put(list.get(3).name.get("title").toString(), list.get(3).name);
        }
        if (list.size()>4) {
            jo.put(list.get(4).name.get("title").toString(), list.get(4).name);
        }
        return jo;
    }

    public static void print(String str) {
        System.out.println(str);
    }
    public static void print(double str) {
        System.out.println(str);
    }
    public static void print(@SuppressWarnings("rawtypes") List str) {
        System.out.println(str.toString());
    }

}

class SentimentComparator implements Comparator<Element> {
    public int compare(Element p1, Element p2){
        if ((p1.sentiment-p2.sentiment)>=0) {
            return 1;
        } else {
            return -1;
        }
    }
}
