package twitter;

import java.util.ArrayList;
import java.util.List;

public class Events {
	
	public String name;
	public List<String> keywords = new ArrayList<String>();
	
	public Events(String name, List<String> keywords) {
		this.name = name;
		this.keywords = keywords;
	}

}
