package wasdev.sample.servlet;

//Classe pour le parsing des JSON sources et l'ajout à la base Cloudant
public class EventDoc {
	
	private String _id;
	private String _rev;
	
	private String title;
	private double[] latlon;
	private String placename;
	private String address;
	private String city;
	private String date_start;
	private String date_end;
	private String pricing_info;
	private String description;
	private String free_text;
	private String link;
	private String image;
	private String updated_at;
	
	//Champ à updater grâce à Alchemy?
	private String tags;
	
	//Champs à générer à l'aide de Personlity Insights, Alchemy, NLC ?
	private String personality;
	private String category;
	
	public void setId(String uid){
		_id = uid;
	}
	
	public void setRev(String rev){
		_rev = rev;
	}
	
	public String getRev(){
		return _rev;
	}
	
	public String getUpdateDate(){
		return updated_at;
	}
	
}
