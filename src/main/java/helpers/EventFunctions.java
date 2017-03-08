package helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import main.bdd;
import main.date;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by hannamegaouel on 06/03/2017.
 */
public class EventFunctions {

    private static NormalizedLevenshtein l = new NormalizedLevenshtein();
    
    // Différents curseurs pour adapter le comportement du middleware
    static double curseur_anciennete = 1;
    static boolean curseur_sentiments = true;
    static double curseur_distance = 0;
    static double curseur_jours_restants = 0;
    
    // Position GPS utilisateur
    static double lat_user = 48.8534100;
    static double lon_user = 2.3488000;
    

    public static double getEventScore(Events event, HashMap<String, Element> allElements, Date currentDate) throws FileNotFoundException {
        double result = 0;
        double score;
        
        double anciennete_post = 1;
        double eloignement_geo = 1;
        double eloignement_tps = 1;
        double poids = 1;
        

        for (int i=0;i<event.keywords.size();i++) {
            for (String key : allElements.keySet()) {
                //score = getScore(key,event.keywords.get(i));
            	
            	
            	// Ancienneté du post ayant engendré le keyword
            	if(curseur_anciennete > 0 && anciennete_post <= 1){
            		long nb_jours = (1 + currentDate.getTime() - allElements.get(key).date.getTime()) / (1000*60*60*24);
            		anciennete_post = getDateScore(nb_jours, curseur_anciennete);
            	}
            	
            	// Prise en compte des sentiments
            	if(curseur_sentiments){
            		poids = allElements.get(key).sentiment;
            	}
            	
            	// Eloignement géographique de l'évènement
            	if(curseur_distance > 0 && curseur_distance <= 1){
            		String latlon = event.name.get("latlon").getAsString();
                    String[] latlonString = latlon.replace("[","").replace("]","").split(",");
                    if (latlonString.length == 2) {
                    	double lat_e = Double.parseDouble(latlonString[0]);
                    	double lon_e = Double.parseDouble(latlonString[1]);
                    	Integer distance = (int) bdd.distance(lat_user, lat_e, lon_user, lon_e);
                		eloignement_geo = getDistanceScore(distance, curseur_distance);
                    }   
            	}
            	
            	
            	// Eloignement temporel de l'évènement
            	if(curseur_jours_restants > 0 && curseur_jours_restants <= 1){
            		String date_start = event.name.get("date_start").getAsString();
            		int jours_restants = 0;
            		try {
						long date_event = date.dateFormat.parse(date_start).getTime();
						long today = (new Date()).getTime();
						jours_restants = (int) ((date_event - today)/(1000*60*60*24));
					} catch (ParseException e) {
						e.printStackTrace();
					}
            		eloignement_tps = getStartTimeScore(jours_restants, curseur_jours_restants);
            	}
                
                
                //result += (allElements.get(key).sentiment)*(score)/getdatescore(diff,datecurseur);
                result += (1-l.distance(key, event.keywords.get(i))) * poids * 1/anciennete_post * 1/eloignement_geo * 1/eloignement_tps;
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
    
    
    // Score partiel relatif à l'ancienneté des posts
    public static double getDateScore(long diff, Double curseur){
    	return (Math.max(diff, 1.0) - 1.0)*curseur + 1.0;	
    }
    
    // Score partiel relatif à l'éloignement géographique des évènements
    public static double getDistanceScore(Integer distance, Double curseur){
    	return (Math.max(distance, 1000)/1000 - 1.0)*curseur + 1.0;	
    }
    
    // Score partiel relatif à l'éloignement temporel des évènements
    public static double getStartTimeScore(int jours_restants, Double curseur){
    	return (Math.max(jours_restants, 15)/15 - 1.0)*curseur + 1.0;	
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
