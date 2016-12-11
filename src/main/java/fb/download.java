package fb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class download {
	
	public static String fbKEY = "EAACEdEose0cBAEXnSo4ZAfhy0NgapXylCuuwONoiOb3JhOZCprKeKtZB2aVnGHX31TMj4uF82ZCq6m1dfomiYZAPYiuUGRZCgnSzjCGAUjsfxZAiALW8GOnjNrJZApwvLZCfaHNhN1zdZCD0DL3BEbOk4ZCcRZClMjLBh9uxNDdM9GXzYgZDZD";
	
	public static String strJoin(String[] aArr, String sSep) {
	    StringBuilder sbStr = new StringBuilder();
	    for (int i = 0, il = aArr.length; i < il; i++) {
	        if (i > 0)
	            sbStr.append(sSep);
	        sbStr.append(aArr[i]);
	    }
	    return sbStr.toString();
	}
	
public static String downloadURL(String[] args, String urlPassed) throws IOException {
	
		String urlString = null;
		if (urlPassed != null) {
			urlString = urlPassed;
		} else {
			String fields = strJoin(args,"%2C");
			//posts,birthday,photos,status,events,likes
			urlString ="https://graph.facebook.com/v2.8/me?access_token="+fbKEY+"&fields="+fields+"&format=json&method=get&pretty=0&suppress_http_code=1";
		}
	
		String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(urlString);

        // Creating an http connection to communicate with url
        urlConnection = (HttpURLConnection) url.openConnection();
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
