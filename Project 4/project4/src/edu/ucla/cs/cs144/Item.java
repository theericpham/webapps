package edu.ucla.cs.cs144;

import java.math.BigDecimal;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Item {

	public Item(String id, String nm, String desc, BigDecimal sp, BigDecimal bp, BigDecimal cp, Date st, Date et) {
		itemId = id;
		name = nm;
		description = desc;
		startPrice = sp;
		buyPrice = bp;
		curPrice = cp;
		startTime = st;
		endTime = et;
		sdf = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
	}

	public String getId() { return itemId; }
	public String getName() { return name; }
	public String getDescription() { return description; }
	public String getStartPrice() { return "$" + startPrice; }
	public String getBuyPrice() { return (buyPrice.intValue() == 0) ? "No Buy Price" : "$" + buyPrice; }
	public String getCurrentPrice() { return "$" + curPrice; }
	public String getStartTime() { return sdf.format(startTime); }
	public String getEndTime() { return sdf.format(endTime); }

	private String itemId;
	private String name;
	private String description;
	private BigDecimal startPrice;
	private BigDecimal buyPrice;
	private BigDecimal curPrice;
	private Date startTime;
	private Date endTime;
	private SimpleDateFormat sdf;
}