package edu.ucla.cs.cs144;

import java.sql.Timestamp;
import java.math.BigDecimal;

public class Bid {
	public Bid(long iid, String bid, Timestamp t, BigDecimal a, int r, String l, String c) {
		itemID   = iid;
		bidderID = bid;
		time     = t;
		amount   = a;
		rating   = r;
		location = l;
		country  = c;
	}

	public long getItemID() { return itemID; }
	public String getBidderID() { return bidderID; }
	public Timestamp getTime() { return time; }
	public BigDecimal getAmount() { return amount; }
	public int getBidderRating() { return rating; }
	public String getBidderLocation() { return location; }
	public String getBidderCountry() { return country; }

	private long itemID;
	private String bidderID;
	private Timestamp time;
	private BigDecimal amount;
	private int rating;
	private String location;
	private String country;
}