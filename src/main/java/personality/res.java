
package personality;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import com.ibm.watson.developer_cloud.personality_insights.v3.model.ConsumptionPreferences;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.Profile;

import helpers.Element;



public class res {
	
		static HashMap<String, Element> resultat=new HashMap<String, Element>();
		public static HashMap<String, Element> getres (String text, Date dd) throws JSONException{
		
		PersonalityInsights service = new PersonalityInsights("2016-10-19");
	    service.setUsernameAndPassword("9dd1e35f-7712-434b-91b7-65a14530dc5f", "Yrx7YZYFc2sL");
	    Profile profile = service.getProfile(text).execute();
	    List<ConsumptionPreferences> l=profile.getConsumptionPreferences();
	    HashMap<String,Double> h=new Parsing().getpreferences(l);
	    for (String key:h.keySet()){
	    	Double aux=h.get(key);
	    	Double score=0.0;
	    	if (aux==0){
	    		score=-1.0;
	    	}else if (aux==0.5){
	    		score=0.0;
	    	}else {
	    		score=1.0;
	    	}
	    	Element e=new Element(score,dd);
	    	String [] subs=key.split(" ");
	    	String str="";
	    	for (int i=3;i<subs.length;i++){
	    		str=str+" "+subs[i];	
	    	}
	    	resultat.put(str,e);   	
	    }
	    return resultat;	
	}
		/*public static void main (String[] args) throws JSONException{
			String ch="As cheesy as it may sound, I draw a lot of inspiration on the daily from the encouraging "
					+ "words of others, and this is particularly true when it comes to quotes about travel.  "
					+ "Though I find plenty of travel quotes inspiring, there are a select few that have stuck with me "
					+ "since the moment I first read them.  I often find myself revisiting them at times when I’m feeling frustrated,"
					+ " confused, or anxious–they serve as good reminders of the things I value and never fail to help me screw my head back on straight."
					+ " They’re like my little life mantras, I guess you could say.";


			Date dd=new Date();
			//System.out.println(getres(ch,dd));
			
			String x = "1488362262"; // created_time tag value goes here.
			long foo = Long.parseLong(x)*1000;
			Date date = new Date(foo);
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			System.out.println(formatter.format(date));
			
			
		}*/

}
