
package personality;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.ibm.watson.developer_cloud.personality_insights.v3.model.ConsumptionPreferences;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.ConsumptionPreferences.ConsumptionPreference;

public class Parsing {
	
	public  HashMap<String,Double> getpreferences(List<ConsumptionPreferences> l) throws JSONException{
		HashMap<String,Double> prefs = new HashMap<String,Double>();
		if (l == null ||  l.size() == 0) {
			return prefs;
		}
		for (int i=0; i<l.size();i++) {
			ConsumptionPreferences x=l.get(i);
			if (x.getName().equals("Movie Preferences")||x.getName().equals("Music Preferences")){  
				List<ConsumptionPreference> l1=x.getConsumptionPreferences();
				for (int j=0; j<l1.size();j++){
				ConsumptionPreference y=l1.get(j);
				String name=y.getName();
				double score=y.getScore();
					prefs.put(name,score);
				}
				}
		}
			
		return prefs;
	}
	
	public static ArrayList<String> getprefs(List<ConsumptionPreferences> l) throws JSONException{
		ArrayList<String> prefs = new ArrayList<String>();
		if (l == null ||  l.size() == 0) {
			return prefs;
		}
		for (int i=0; i<l.size();i++) {
			ConsumptionPreferences x=l.get(i);
			List<ConsumptionPreference> l1=x.getConsumptionPreferences();
			for (int j=0; j<l1.size();j++){
				ConsumptionPreference y=l1.get(j);
				String name=y.getName();
				double score=y.getScore();
				if (score>=0.5){
					prefs.add(name);
				}
			}
			}
			
		return prefs;
	}

}
