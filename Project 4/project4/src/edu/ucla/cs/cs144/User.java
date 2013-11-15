package edu.ucla.cs.cs144;

public class User {

	public User(String id, int rat, String loc, String cty) {
		userId = id;
		rating = rat;
		location = loc;
		country = cty;
	}

	public String getId() { return userId; }
	public int getRating() { return rating; }
	public String getLocation() { return location; }
	public String getCountry() { return country; }
	public String getLocationCountry() { return (location.equals("") && country.equals("")) ? "Unknown" : location + ", " + country; }

	private String userId;
	private int rating;
	private String location;
	private String country;
}