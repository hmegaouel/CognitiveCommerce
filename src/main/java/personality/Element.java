

import java.util.Date;

public class Element {
	
	public Date date;
	public double sentiment;
	
	public Element(double s, Date d) {
		this.date = d;
		this.sentiment = s;
	}
	
	
	public Element (double s){
		this.sentiment=s;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public void setSentiment(double sent) {
		this.sentiment = sent;
	}
	
	public String toString() {
		return "{"+sentiment+";"+date+"}";
	}

}