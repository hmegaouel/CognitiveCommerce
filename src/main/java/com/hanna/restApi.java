package com.hanna;
import analysis.functions;
import fb.Facebook;
import fb.image;
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
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Path("api")
public class restApi {
    final List<Events> events = new ArrayList<Events>();

    @XmlRootElement
    public class MyJaxBean {
        @XmlElement
        public String twitterUsername;
        @XmlElement
        public String fbToken;
    }

    @GET
    @Path("/test")
    public Response showTest() {
        return Response.status(200).entity("All ok").build();
    }

    @POST @Consumes("application/json")
    @Path("/event")
    public Response event(final MyJaxBean input) throws ParseException, IOException {
        String twitterUsername = input.twitterUsername;
        String fbToken = input.fbToken;
        events.add(new Events("PGW",new ArrayList<String>(Arrays.asList("game","video game","GTA","Call of duty","enemies"))));
        events.add(new Events("Hackaton", new ArrayList<String>(Arrays.asList("programming","java","prize","computer","hack"))));
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
        return Response.status(200).entity(best).build();
    }


    @GET
    @Path("/twitter/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTwitter(@PathParam("param") String twitterUsername) throws ParseException, IOException {
        events.add(new Events("PGW",new ArrayList<String>(Arrays.asList("game","video game","GTA","Call of duty","enemies"))));
        events.add(new Events("Hackaton", new ArrayList<String>(Arrays.asList("programming","java","prize","computer","hack"))));
        final Date date = new date().getDate();
        tweet twit = new tweet(twitterUsername,date);
        twit.getTweets(null);
        HashMap<String, Element> allResult = new HashMap<String, Element>();
        allResult.putAll(twit.result);
        String best = functions.getBestEvent(events, allResult, date).toString();
        return Response.status(200).entity(best).build();
    }


    @GET
    @Path("/fb/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFb(@PathParam("param") String fbToken) throws ParseException, IOException {
        events.add(new Events("PGW",new ArrayList<String>(Arrays.asList("game","video game","GTA","Call of duty","enemies"))));
        events.add(new Events("Hackaton", new ArrayList<String>(Arrays.asList("programming","java","prize","computer","hack"))));
        final Date date = new date().getDate();
        Facebook fb = new Facebook(date, fbToken);
        fb.processPosts(null);
        fb.processResults();
        HashMap<String, Element> allResult = new HashMap<String, Element>();
        allResult.putAll(fb.result);
        String best = functions.getBestEvent(events, allResult, date).toString();
        return Response.status(200).entity(best).build();
    }
}