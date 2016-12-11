package twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.sun.org.apache.xml.internal.security.utils.Base64;

public class download {
	
	private static String alchemyAPIKey = "33295d5ddf57834ef58d33bef5161d46db0e4e49";
	private static String twitterUser = "79d21e02-98fb-4486-89dc-05e8b7256789";
	private static String twitterPass = "bBdtlQg6vx";
	
	public static String alchemyPostRequest(String urlString, String stuff) throws IOException {
		
		//tweet.print("Post from "+urlString);
		
		String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(urlString+"?apikey="+alchemyAPIKey);

	        // Creating an http connection to communicate with url
	        urlConnection = (HttpURLConnection) url.openConnection();
	        String userpass = twitterUser+":"+twitterPass;
	        String basicAuth = "Basic " + new String(Base64.encode(userpass.getBytes()));
	        urlConnection.setRequestMethod("POST");
	        urlConnection.setRequestProperty("Authorization", basicAuth);
	        urlConnection.setDoOutput(true);
	        // Connecting to url
	        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());

	        writer.write(stuff);
	        writer.flush();
	
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
        String basicAuth = "Basic " + new String(Base64.encode(userpass.getBytes()));
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
