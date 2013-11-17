package edu.ucla.cs.cs144;

import java.math.BigDecimal;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Bid {

	public Bid(User b, BigDecimal amt, Date t) {
		bidder = b;
		amount = amt;
		time = t;
		sdf = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
	}

	public User getBidder() { return bidder; }
	public String getAmount() { return "$" + amount; }
	public String getTime() { return sdf.format(time); }

	private User bidder;
	private BigDecimal amount;
	private Date time;
	private SimpleDateFormat sdf;
}