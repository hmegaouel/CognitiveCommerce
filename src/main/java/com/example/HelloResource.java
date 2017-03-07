package com.example;

import instagram.Instagram;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import fb.Facebook;
import fb.image;
import main.bdd;
import main.date;
import personality.*;

import org.json.JSONException;

import helpers.Element;
import helpers.Events;
import twitter.tweet;

import static helpers.EventFunctions.getBestEvent;
import static helpers.ImageFunctions.getImageTags;


// This class define the RESTful API to fetch the database service information
// <basepath>/api/hello

@Path("/hello")
public class HelloResource {


	//RecupCoordGPS gps = new RecupCoordGPS();
	//double[] coords = null;
	final main.bdd bdd = new bdd(48.8534100, 2.3488000);
	static int connections;
	int distance = 20000;
	String dateString;

	@GET
	@Path("/test")
	public String showTest() throws IOException, JSONException {
//		download d = new download();
//		String outp = d.downloadURL("https://cdeservice.eu-gb.mybluemix.net/api/v1/messages/search?q=from:KimKardashian&size=100");
//		System.out.println(outp);
		JSONObject myJSONObj = new JSONObject();
		myJSONObj.put("okay", "true");
		return myJSONObj.toString();
	}

	@GET
	@Path("/set/{distance}/{date}")
	public String set(@PathParam("distance") String distance, @PathParam("date") String dateString) throws IOException, JSONException {
		if (dateString != "") {
			this.dateString = dateString; //de la forme annee-mois-jour
		}
		if (distance != "") {
			this.distance = Integer.parseInt(distance); //rayon en mètres
		}
		bdd.setParams(this.distance, this.dateString);
		JSONObject myJSONObj = new JSONObject();
		myJSONObj.put("date", this.dateString);
		myJSONObj.put("radius", this.distance);
		return myJSONObj.toString();
	}

	@GET
	@Path("/bdd")
	public String showBdd() throws IOException, JSONException {
		List<Events> bddDocs = bdd.searchByCity();
		JSONObject myJSONObj = new JSONObject();
		for(Events e : bddDocs) {
			myJSONObj.put(e.name.toString(), Arrays.toString(e.keywords.toArray()));
		}
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
		Instagram insta = new Instagram();
		List<String> images = insta.getUserImages(Instagram.access_token);
		HashMap<String,Double> tags = getImageTags(images.get(0));
		System.out.println(tags);
		//
		
		HashMap<String, Element> allResult = new HashMap<String, Element>();
		allResult.putAll(fb.result);
		allResult.putAll(twit.result);
		image im = new image(fbToken);
		im.processImages(null);
		allResult.putAll(im.imageDb);
		String best = getBestEvent(events, allResult, date).toString();
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
		String best = getBestEvent(events, allResult, date).toString();
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
		HashMap<String, Element> allResult = new HashMap<String, Element>();
		allResult.putAll(fb.result);
		String best = getBestEvent(events, allResult, date).toString();
		connections--;
		System.out.println("Closing, connections left: "+connections);
		return best;
	}

}
