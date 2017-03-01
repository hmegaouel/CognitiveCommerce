package com.example;

import instagram.Instagram;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import analysis.functions;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

import fb.Facebook;
import fb.image;
import main.RecupCoordGPS;
import main.bdd;
import main.date;

import org.json.JSONException;

import twitter.Element;
import twitter.Events;
import twitter.download;
import twitter.tweet;


// This class define the RESTful API to fetch the database service information
// <basepath>/api/hello

@Path("/hello")
public class HelloResource {


	//RecupCoordGPS gps = new RecupCoordGPS();
	//double[] coords = null;
	final main.bdd bdd = new bdd(48.8534100, 2.3488000);
	static int connections;

	@GET
	@Path("/test")
	public String showTest() throws IOException {
		download d = new download();
		String outp = d.downloadURL("https://cdeservice.eu-gb.mybluemix.net/api/v1/messages/search?q=from:KimKardashian&size=100");
		System.out.println(outp);
		JSONObject myJSONObj = new JSONObject();
		myJSONObj.put("okay", "true");
		return myJSONObj.toString();
	}

/*	@GET
	@Path("/gps")
	public String showGPS() {
		try {
			coords = gps.GPSSetter();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		JSONObject myJSONObj = new JSONObject();
		myJSONObj.put("lat", coords[0]);
		myJSONObj.put("lon", coords[1]);
		return myJSONObj.toString();
	}*/

	
	
	
	
	/*
	 * Instagram ajouté. Pour l'instant on utilise un token codé en dur, car une autorisation d'accès à instagram doit être obtenue coté appli mobile pour chaque nouvel utilisateur
	 */
	
	@GET
	@Path("/event/{twitter}/{fb}/{insta}")
	@Produces(MediaType.APPLICATION_JSON)
	public String event(@PathParam("twitter") String twitterUsername, @PathParam("fb") String fbToken, @PathParam("insta") String instaToken) throws ParseException, IOException, JSONException {
		connections++;
		System.out.println("Connections: "+connections);
		List<Events> events = bdd.searchByCity();
		final Date date = new date().getDate();
		tweet twit = new tweet(twitterUsername,date);
		twit.getTweets(null);
		Facebook fb = new Facebook(date, fbToken);
		fb.processPosts(null);
		fb.processResults();
		
		//Exemple tags de la première image du compte instagram "cog.comm"
		//
		Instagram insta = new Instagram();
		List<String> images = insta.getUserImages(Instagram.access_token);
		HashMap<String,Double> tags = functions.getImageTags(images.get(0));
		System.out.println(tags);
		//
		
		HashMap<String, Element> allResult = new HashMap<String, Element>();
		allResult.putAll(fb.result);
		allResult.putAll(twit.result);
		image im = new image(fbToken);
		im.processImages(null);
		allResult.putAll(im.imageDb);
		String best = functions.getBestEvent(events, allResult, date).toString();
		connections--;
		System.out.println("Closing, connections left: "+connections);
		return best;
	}

	@GET
	@Path("/twitter/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTwitter(@PathParam("param") String twitterUsername) throws ParseException, IOException, JSONException {
		connections++;
		System.out.println("Connections: "+connections);
		List<Events> events = bdd.searchByCity();
		System.out.println("Bdd data : "+events.size());
		final Date date = new date().getDate();
		tweet twit = new tweet(twitterUsername,date);
		twit.getTweets(null);
		System.out.println("Done getting tweets, checking results");
		HashMap<String, Element> allResult = new HashMap<String, Element>();
		allResult.putAll(twit.result);
		String best = functions.getBestEvent(events, allResult, date).toString();
		connections--;
		System.out.println("Closing, connections left: "+connections);
		return best;
	}


	@GET
	@Path("/fb/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getFb(@PathParam("param") String fbToken) throws ParseException, IOException, JSONException {
		connections++;
		System.out.println("Connections: "+connections);
		List<Events> events = bdd.searchByCity();
		final Date date = new date().getDate();
		Facebook fb = new Facebook(date, fbToken);
		fb.processPosts(null);
		fb.processResults();
		HashMap<String, Element> allResult = new HashMap<String, Element>();
		allResult.putAll(fb.result);
		String best = functions.getBestEvent(events, allResult, date).toString();
		connections--;
		System.out.println("Closing, connections left: "+connections);
		return best;
	}

/*	@GET
	@Path("/test")
	@Produces("application/json")
	public String getInformation() throws Exception, IOException {
        JSONObject myJSONObj = new JSONObject();
        myJSONObj.put("message", "Hello World!");
        return myJSONObj.toString();
        
	}*/
}