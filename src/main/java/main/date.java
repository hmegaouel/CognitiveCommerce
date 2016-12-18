package main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class date {
	
	public final static DateFormat twitterinputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
	public final static DateFormat fbinputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	public final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	public Date getDate(){
		Date date = new Date();
		return date;
	}

}
