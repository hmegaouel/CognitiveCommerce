package main;

import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;


public class RecupCoordGPS {

	public RecupCoordGPS ()  {
	}
	
	//DETERMINER LIEU DEPART VIA COORD GPS VIA API GOOGLE:
	
	public double[] GPSSetter() throws JSONException, IOException {
		
	System.out.println("Geolocalisation en cours...");
	String url = "https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyBerSJRIAP3y1IYNULKRc1AeYKEtG_zTkk";
    MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
    //formData.add("macAddress", "C4:8E:8F:09:FF:D5");
    String output = new Request("POST", url, "", new String[]{}).getData();
    

    
    if (output != null) {
    	
    	JSONObject obj;
    	obj = new JSONObject(output);
    	
    	if (!obj.has("error")) { //erreur de google
        	JSONObject location= obj.getJSONObject("location");
        	Double lat = location.getDouble("lat");
        	Double lng = location.getDouble("lng");

			System.out.println(lat+";"+lng);

        	double resGPS[] = {lat,lng};
            return resGPS;
    	}
    	
    	
    }
    
    return null;
    
    
	}  
	

	

}
