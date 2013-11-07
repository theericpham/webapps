package edu.ucla.cs.cs144;

import java.sql.Timestamp;

public class Bid {
	public Bid(int iid, String bid, Timestamp t, double a) {
		itemID   = iid;
		bidderID = bid;
		time     = t;
		amount   = a;
	}

	public int getItemID() { return itemID; }
	public String getbidderID() { return bidderID; }
	public Timestamp getTime() { return time; }
	public double getAmount() { return amount; }

	private int itemID;
	private String bidderID;
	private Timestamp time;
	private double amount;
}