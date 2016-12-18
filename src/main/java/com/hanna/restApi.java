package com.hanna;
import fb.Facebook;
import main.date;
import twitter.Events;
import twitter.tweet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Path("/api")
public class restApi {
    final List<Events> events = new ArrayList<Events>();

    @GET
    @Path("/test")
    public Response showTest() {
        return Response.status(200).entity("All ok").build();
    }

    @GET
    @Path("/twitter/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTwitter(@PathParam("param") String twitterUsername) throws ParseException, IOException{
        events.add(new Events("PGW",new ArrayList<String>(Arrays.asList("game","video game","GTA","Call of duty","enemies"))));
        events.add(new Events("Hackaton", new ArrayList<String>(Arrays.asList("programming","java","prize","computer","hack"))));
        final Date date = new date().getDate();
        tweet twit = new tweet(twitterUsername,date);
        twit.getTweets(null);
        String res = twit.getEvent(events);
        return Response.status(200).entity(res).build();
    }

    @GET
    @Path("/fb/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFb(@PathParam("param") String fbToken) throws ParseException, IOException{
        events.add(new Events("PGW",new ArrayList<String>(Arrays.asList("game","video game","GTA","Call of duty","enemies"))));
        events.add(new Events("Hackaton", new ArrayList<String>(Arrays.asList("programming","java","prize","computer","hack"))));
        final Date date = new date().getDate();
        Facebook fb = new Facebook(date, fbToken);
        fb.processPosts(null);
        fb.processResults();
        String res = fb.getEvent(events);
        return Response.status(200).entity(res).build();
    }
}