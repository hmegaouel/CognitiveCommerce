package twitter;

import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class download {
	
	private static String alchemyAPIKey = "4a2e14d80c21f4334269db52847c5ca57bab7448";
	private static String twitterUser = "79d21e02-98fb-4486-89dc-05e8b7256789";
	private static String twitterPass = "bBdtlQg6vx";
	
	public static String alchemyPostRequest(String urlString, String stuff) throws IOException {
		
		tweet.print("Post from "+urlString);
		tweet.print("Stuff:");
		tweet.print(stuff);
		String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(urlString+"?apikey="+alchemyAPIKey);

	        // Creating an http connection to communicate with url
	        urlConnection = (HttpURLConnection) url.openConnection();
	        String userpass = twitterUser+":"+twitterPass;
	        String basicAuth = "Basic " + new String(Base64.encodeBase64(userpass.getBytes()));
	        urlConnection.setRequestMethod("POST");
	        urlConnection.setRequestProperty("Authorization", basicAuth);
	        urlConnection.setDoOutput(true);
	        // Connecting to url
	        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());

			System.out.println("post parameters");
	        writer.write(stuff);
	        writer.flush();
			urlConnection.getOutputStream().close();
	
	        // Reading data from url
	        iStream = urlConnection.getInputStream();
	
	        BufferedReader br = new BufferedReader(new InputStreamReader(iStream, "UTF-8"));
	
	        StringBuffer sb = new StringBuffer();
	
	        String line = "";
	        while( ( line = br.readLine()) != null){
	            sb.append(line);
	        }
	
	        data = sb.toString();
			System.out.println("DATA");
			System.out.println(data);
	
	        br.close();

	    } catch(Exception e){
	        System.out.println("Exception while downloading url"+ e.toString());
	    } finally{
	        iStream.close();
	        urlConnection.disconnect();
	    }
	    return data;
		
	}
	
public static String downloadURL(String urlString) throws IOException {
	
		//tweet.print("Download from "+urlString);
	
		String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(urlString);

        // Creating an http connection to communicate with url
        urlConnection = (HttpURLConnection) url.openConnection();
        String userpass = twitterUser+":"+twitterPass;
        String basicAuth = "Basic " + new String(Base64.encodeBase64(userpass.getBytes()));
        urlConnection.setRequestProperty("Authorization", basicAuth);
        // Connecting to url
        urlConnection.connect();

        // Reading data from url
        iStream = urlConnection.getInputStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(iStream, "UTF-8"));

        StringBuffer sb = new StringBuffer();

        String line = "";
        while( ( line = br.readLine()) != null){
            sb.append(line);
        }

        data = sb.toString();

        br.close();

    }catch(Exception e){
        System.out.println("Exception while downloading url"+ e.toString());
    }finally{
        iStream.close();
        urlConnection.disconnect();
    }
    return data;
	}

}
