package com.hanna;
import analysis.functions;
import fb.Facebook;
import fb.image;
import main.RecupCoordGPS;
import main.bdd;
import main.date;
import org.json.JSONObject;
import twitter.Element;
import twitter.Events;
import twitter.tweet;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Path("api")
public class restApi {

    RecupCoordGPS gps = new RecupCoordGPS();
    double[] coords = gps.GPSSetter();
    final bdd bdd = new bdd(coords[0], coords[1]);
    static int connections;

    @GET
    @Path("/test")
    public Response showTest() throws IOException {
        return Response.status(200).entity("All ok").build();
    }

    @GET
    @Path("/gps")
    public Response showGPS() {
        return Response.status(200).entity(coords[0]+","+coords[1]).build();
    }

    @GET
    @Path("/event/{twitter}/{fb}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response event(@PathParam("twitter") String twitterUsername, @PathParam("fb") String fbToken) throws ParseException, IOException {
        connections++;
        System.out.println("Connections: "+connections);
        List<Events> events = bdd.searchByCity();
        final Date date = new date().getDate();
        tweet twit = new tweet(twitterUsername,date);
        twit.getTweets(null);
        Facebook fb = new Facebook(date, fbToken);
        fb.processPosts(null);
        fb.processResults();
        HashMap<String, Element> allResult = new HashMap<String, Element>();
        allResult.putAll(fb.result);
        allResult.putAll(twit.result);
        image im = new image(fbToken);
        im.processImages(null);
        allResult.putAll(im.imageDb);
        String best = functions.getBestEvent(events, allResult, date).toString();
        connections--;
        System.out.println("Closing, connections left: "+connections);
        return Response.status(200).entity(best).build();
    }

    @GET
    @Path("/twitter/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTwitter(@PathParam("param") String twitterUsername) throws ParseException, IOException {
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
        return Response.status(200).entity(best).build();
    }


    @GET
    @Path("/fb/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFb(@PathParam("param") String fbToken) throws ParseException, IOException {
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
        return Response.status(200).entity(best).build();
    }
}