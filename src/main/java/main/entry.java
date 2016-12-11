package main;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import fb.Facebook;

import twitter.Events;
import twitter.tweet;

public class entry {

	public static void main(String[] args) throws IOException, JSONException, ParseException {
		
		final Date date = new date().getDate();
		
		final List<Events> events = new ArrayList<Events>();
		events.add(new Events("PGW",new ArrayList<String>(Arrays.asList("game","video game","GTA","Call of duty","enemies"))));
		events.add(new Events("Hackaton", new ArrayList<String>(Arrays.asList("programming","java","prize","computer","hack"))));
		
		Thread twitter = new Thread(new Runnable(){
			public void run(){
				tweet twit = new tweet("WongeneKIM",date);
				try {
					twit.getTweets(null);
					twit.processResults();
					twit.getEvent(events);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		Thread facebook = new Thread(new Runnable(){
			public void run(){
				Facebook fb = new Facebook(date);
				try {
					fb.processPosts(null);
					fb.processResults();
					fb.getEvent(events);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		twitter.start();
		facebook.start();

	}

}
