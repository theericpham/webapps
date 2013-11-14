package edu.ucla.cs.cs144;

import java.math.BigDecimal;
import java.util.Date;

public class Bid {

	public Bid(long id, String bid, BigDecimal amt, Date t) {
		itemId = id;
		bidderId = bid;
		amount = amt;
		time = t;
	}

	public long getId() { return itemId; }
	public String getBidderId() { return bidderId; }
	public BigDecimal getAmount() { return amount; }
	public Date getTime() { return time; }

	private long itemId;
	private String bidderId;
	private BigDecimal amount;
	private Date time;
}