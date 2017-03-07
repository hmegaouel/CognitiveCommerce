

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import com.ibm.watson.developer_cloud.personality_insights.v3.model.ConsumptionPreferences;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.Profile;



public class res {
	
		static HashMap<String, Element> resultat=new HashMap<String, Element>();
		public static HashMap<String, Element> getres (String text) throws JSONException{
		
		PersonalityInsights service = new PersonalityInsights("2016-10-19");
	    service.setUsernameAndPassword("9dd1e35f-7712-434b-91b7-65a14530dc5f", "Yrx7YZYFc2sL");
	    Profile profile = service.getProfile(text).execute();
	    List<ConsumptionPreferences> l=profile.getConsumptionPreferences();
	    HashMap<String,Double> h=new Parsing().getpreferences(l);
	    for (String key:h.keySet()){
	    	Element e=new Element(1.0);
	    	String [] subs=key.split(" ");
	    	String str="";
	    	for (int i=3;i<subs.length;i++){
	    		str=str+" "+subs[i];	
	    	}
	    	resultat.put(str,e);   	
	    }
	    return resultat;	
	}

}
