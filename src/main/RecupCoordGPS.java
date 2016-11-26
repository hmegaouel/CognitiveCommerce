package main;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.json.JSONException;
import org.json.JSONObject;


public class RecupCoordGPS {

	public RecupCoordGPS ()  {
	}
	
	//DETERMINER LIEU DEPART VIA COORD GPS VIA API GOOGLE:
	
	public double[] GPSSetter() throws JSONException {
		
	System.out.println("Geolocalisation en cours...");
	String url = "https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyBerSJRIAP3y1IYNULKRc1AeYKEtG_zTkk";
    MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
    //formData.add("macAddress", "C4:8E:8F:09:FF:D5");
    String output = new PostRequest(url,formData).run();  // Requete POST de Google pour obtenir la localisation du user
    

    
    if (output != null) {
    	
    	JSONObject obj;
    	obj = new JSONObject(output);
    	
    	if (!obj.has("error")) { //erreur de google
        	JSONObject location= obj.getJSONObject("location");
        	Double lat = location.getDouble("lat");
        	Double lng = location.getDouble("lng");

        	double resGPS[] = {lat,lng};
            return resGPS;
    	}
    	
    	
    }
    
    return null;
    
    
	}  
	

	

}
