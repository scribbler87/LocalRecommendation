package fi.uni.aalto.controllers;




public class Post {
	

	private String title;
	private String user;
	private String url;
	private String date;
	private String description;
	
	private int latitude;
	private int longitude;
	
	public int getLongitude() {
		return longitude;
	}
	public int getLatitude() {
		return latitude;
	}

	public void setLongtitude(int longitude) {
		this.longitude = longitude;
		
	}
	public void setLatitude(int latitude) {
		this.latitude = latitude;
		
	}
	
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
//	public void setDate(String date) {
//		if(date.length() >0 )
//			this.date = new Date(date); 
//	}
	
	
	
	

}
