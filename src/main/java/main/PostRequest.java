package main;



//Cette classe permet de regrouper le modèle d'une requete POST
//qui sera utilisée lors des différents appels

import org.glassfish.jersey.client.ClientProperties;

import javax.swing.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class PostRequest {
	
	private String url;
	private MultivaluedMap<String, String> formdata;
	
	public PostRequest(String url, MultivaluedMap<String, String> formdata) {
		
		this.url = url;
		this.formdata = formdata;
		
	}
	
	public String run() {
		
		String json = null;
		Client client = ClientBuilder.newClient();
		client.property(ClientProperties.CONNECT_TIMEOUT, 10000); // si pas internet, apres un délai d'attente raisonnable : exception
		WebTarget webTarget = client.target(url);
		try {
			json = webTarget.request().post(Entity.form(formdata), String.class);
		} catch (Exception e) {
			System.out.println("Pas de connexion internet");
			JOptionPane.showMessageDialog(null,"Pas de connexion internet");
		}
		
		return json;
		
	}

}
