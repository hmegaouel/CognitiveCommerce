package wasdev.sample.servlet;

import com.cloudant.client.api.*;
import com.google.gson.*;

public class Dbase {

	public static void main(String[] args) {
		
		//Connexion au service Cloudant
		CloudantClient client = ClientBuilder.account("3f53d18a-bd59-463c-9eca-5c37a5d02e5a-bluemix")
                .username("3f53d18a-bd59-463c-9eca-5c37a5d02e5a-bluemix")
                .password("e3aa68cd6ea3b4446e24fd42efda116fb37bfe2ecdd914f2ad31a44405c7910f")
                .build();
		
		
		//Affichage de la version du serveur pour vérifier qu'on est bien connecté
		System.out.println("Server Version: " + client.serverVersion());

		
		//Supprimer et recréer la collection "events" si besoin
		/*
		client.deleteDB("events");
		client.createDB("events");
		*/
		
		
		//Accès à la collection "events"
		Database db = client.database("events", false);
		
		
		//Test d'interrogation de la base : récupération de certains champs des documents dont les tags contiennent "lecture" 
		/*
		1List<JsonObject> res = db.findByIndex("{ \"selector\": {\"tags\": \"lecture\"},\"fields\": [\"title\",\"city\",\"date_start\"] }", JsonObject.class);
		for(JsonObject o : res){
			System.out.println(o);
		}
		*/
		
		
		//Récupération de 3 mois d'évènements depuis OpenDataSoft		
		JsonArray eventsArray = Events.getEvents();
		

		//Pärsing des résultats
		Gson gson = new Gson();
		for (int i=0; i<eventsArray.size(); i++){
        	JsonObject singleEvent = eventsArray.get(i).getAsJsonObject().get("fields").getAsJsonObject();
        	String id = singleEvent.get("uid").getAsString();
        	EventDoc doc = gson.fromJson(singleEvent, EventDoc.class);
    		doc.setId(id);
    		//Gestion des updates
        	if(!db.contains(id)){
        		db.save(doc);
        		System.out.println("Added event " + id + " !");
        	}
        	else{
        		EventDoc existing_doc = db.find(EventDoc.class, id);
        		String rev = existing_doc.getRev();
        		if(!doc.getUpdateDate().equals(existing_doc.getUpdateDate())){
        			doc.setRev(rev);
        			existing_doc = doc;
        			db.update(existing_doc);
        			System.out.println("Updated event " + id + " !");
        		}
        		else{
        			System.out.println("Event  " + id + " already up-to-date !");
        		}
        	}
		}

		
	}
	

}
