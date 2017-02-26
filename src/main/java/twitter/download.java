package twitter;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.json.JSONException;
import personality.Insights;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;

public class download {
	
	private static String alchemyAPIKey = "4a2e14d80c21f4334269db52847c5ca57bab7448";
	private static String twitterUser = "82c55037-92c8-421f-8d4c-9c9e56b68809";
	private static String twitterPass = "e6TaVDzihc";
	
	public static String alchemyPostRequest(String urlString, String stuff) throws IOException {

//		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(twitterUser, twitterPass);
//		ClientConfig clientConfig = new ClientConfig();
//		clientConfig.register(feature) ;
//		String url = urlString+"?apikey="+alchemyAPIKey+"&"+stuff;
//		System.out.println("url is: "+url);
//		Client client = ClientBuilder.newClient(clientConfig);
//		WebTarget webTarget = client.target(url);
//		String response = webTarget.request().get(String.class);
//		return response;

		String data = "";
		String behind = "?apikey="+alchemyAPIKey+"&"+stuff;
		String encodedStuff=java.net.URLEncoder.encode(behind,"UTF-8");
		String urlS = urlString+encodedStuff;
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;

		try{
			// Connexion
			URL url = new URL(urlS);
			urlConnection = (HttpURLConnection) url.openConnection();
			String userpass = twitterUser+":"+twitterPass;
			String basicAuth = "Basic " + new String(Base64.encodeBase64(userpass.getBytes()));
			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("Authorization", basicAuth);
			urlConnection.connect();

			// Lecture de la réponse
			iStream = urlConnection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(iStream, "UTF-8"));
			StringBuffer sb = new StringBuffer();
			String line = "";
			while( ( line = br.readLine()) != null){
				sb.append(line);
			}

			data = sb.toString();
			br.close();
		}

		catch(Exception e){
			System.out.println("Exception while downloading url"+ e.toString());
		}

		finally{
			if (iStream != null) {
				iStream.close();
			}
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}

		return data;

	}
	
public static String downloadURL(String urlString) throws IOException {

//		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(twitterUser, twitterPass);
//        ClientConfig clientConfig = new ClientConfig();
//        clientConfig.register(feature) ;
//        Client client = ClientBuilder.newClient(clientConfig);
//        WebTarget webTarget = client.target(urlString);
//        String response = webTarget.request().get(String.class);
//        return response;

	String data = "";
	InputStream iStream = null;
	HttpURLConnection urlConnection = null;

	try{
		// Connexion
		//String encodedURL=java.net.URLEncoder.encode(urlString,"UTF-8");
		URL url = new URL(urlString);
		urlConnection = (HttpURLConnection) url.openConnection();
		String userpass = twitterUser+":"+twitterPass;
		String basicAuth = "Basic " + new String(Base64.encodeBase64(userpass.getBytes()));
		urlConnection.setRequestMethod("GET");
		urlConnection.setRequestProperty("Authorization", basicAuth);
		urlConnection.connect();

		// Lecture de la réponse
		iStream = urlConnection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(iStream, "UTF-8"));
		StringBuffer sb = new StringBuffer();
		String line = "";
		while( ( line = br.readLine()) != null){
			sb.append(line);
		}

		data = sb.toString();
		br.close();
	}

	catch(Exception e){
		System.out.println("Exception while downloading url"+ e.toString());
	}

	finally{
		if (iStream != null) {
			iStream.close();
		}
		if (urlConnection != null) {
			urlConnection.disconnect();
		}
	}

	return data;

	}

	public static void main(String [ ] args) throws JSONException, IOException, ParseException {
		download d = new download();
		for (int i=0;i<10;i++) {
			String outp = d.downloadURL("http://cdeservice.eu-gb.mybluemix.net/api/v1/messages/search?q=from:KimKardashian&size=100");
			System.out.println(outp);
		}
	}

}
