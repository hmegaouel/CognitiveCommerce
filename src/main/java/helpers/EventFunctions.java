package helpers;

import com.google.gson.JsonObject;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by hannamegaouel on 06/03/2017.
 */
public class EventFunctions {

    private static NormalizedLevenshtein l = new NormalizedLevenshtein();

    public static double getEventScore(Events event, HashMap<String, Element> allElements, Date currentDate) throws FileNotFoundException {
        double result = 0;
        double score;
        //System.out.println("Getting score");
        //System.out.println(allElements.size());
        for (int i=0;i<event.keywords.size();i++) {
            for (String key : allElements.keySet()) {
                //score = getScore(key,event.keywords.get(i));
                long diff = (1+currentDate.getTime()-allElements.get(key).date.getTime())/(1000*60*60*24);
                //result += (allElements.get(key).sentiment)*(score)/diff;
                result += (allElements.get(key).sentiment)*(1-l.distance(key, event.keywords.get(i)))/diff;
            }
        }
        return result;
    }

    public static double getScore(String word1, String word2) throws FileNotFoundException {
        //return WordVectorSerializer.loadFullModel("model_fr.txt").similarity(word1, word2);
        return 1.0;
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

    public static JSONObject getBestEvent(List<Events> events, HashMap<String, Element> allElements, Date currentDate) throws JSONException, FileNotFoundException {
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

}
