package edu.ucla.cs.cs144;

import java.sql.Timestamp;

public class Bid {
	public Bid(int iid, String bid, Timestamp t, double a, int r, String l, String c) {
		itemID   = iid;
		bidderID = bid;
		time     = t;
		amount   = a;
		rating   = r;
		location = l;
		country  = c;

	}

	public int getItemID() { return itemID; }
	public String getBidderID() { return bidderID; }
	public Timestamp getTime() { return time; }
	public double getAmount() { return amount; }
	public int getBidderRating() { return rating; }
	public String getBidderLocation() { return location; }
	public String getBidderCountry() { return country; }

	private int itemID;
	private String bidderID;
	private Timestamp time;
	private double amount;
	private int rating;
	private String location;
	private String country;
}