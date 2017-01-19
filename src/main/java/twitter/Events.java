package twitter;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Events {
	
	public JsonObject name;
	public List<String> keywords = new ArrayList<String>();
	
	public Events(JsonObject name, List<String> keywords) {
		this.name = name;
		this.keywords = keywords;
	}

}
