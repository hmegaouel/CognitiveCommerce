package wasdev.sample.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import com.google.gson.*;


public class Events {
	
	
	//Récupération de 3 mois d'évènements sur OpenDataSoft
	public static JsonArray getEvents(){
		
		JsonArray month_records = null;
		JsonArray records = new JsonArray();
		String baseurl_search = "https://public.opendatasoft.com/api/records/1.0/search/?dataset=evenements-publics-cibul&facet=date_start&refine.date_start=";
		//Paramètres de la recherche qu'on peut changer pour affiner les résultats
		String params = "&sort=date_start&rows=9999";
		//Mois courant + 2 mois suivants
		String[] months = getThreeMonths();
		
		
		//Récupération des données pour chaque mois
		for(String m : months){
			
			try {
				
				//Connexion via l'API OpenDataSoft
				URL url = new URL(baseurl_search + m + params);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		        urlConnection.connect();
		        InputStream stream = urlConnection.getInputStream();
		        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		        StringBuffer sb = new StringBuffer();

		        String line = "";
		        while( ( line = br.readLine()) != null){
		            sb.append(line);
		            
		        }
		        String data = sb.toString();
		        br.close();

		        //Parsing des résultats d'un mois
		        JsonParser parser = new JsonParser();
		        JsonObject server_answer = (JsonObject) parser.parse(data);
		        month_records = server_answer.getAsJsonArray("records");
		        
		        //Ajout à la liste globale des résultats de 3 mois
		        records.addAll(month_records);
		        
			} catch (MalformedURLException e) {
				System.out.println("Problem with the URL");
			} catch (IOException e) {
				System.out.println("Problem connecting to the API");
			}
			
		}
		
		return records;
		
	}
	
	
	//Retourner le mois courant + les deux mois suivants sous la forme "yyyy-mm"
	public static String[] getThreeMonths(){
		String[] result = new String[3];
		
		int current_month = Calendar.getInstance().get(Calendar.MONTH)+1;
		int current_year = Calendar.getInstance().get(Calendar.YEAR);
		
		String month1 = current_year + "-" + current_month;
		String month2 = "";
		String month3 = "";
		
		if(current_month < 11){
			month2 = current_year + "-" + (current_month+1);
			month3 = current_year + "-" + (current_month+2);
		}
		else if(current_month == 11){
			month2 = current_year + "-12";
			month3 = (current_year+1) + "-1";
		}
		else{
			month2 = (current_year+1) + "-1";
			month3 = (current_year+1) + "-2";
		}

		result[0] = month1;
		result[1] = month2;
		result[2] = month3;
		
		return result;
	}
	
	
	
	
	
	

}
