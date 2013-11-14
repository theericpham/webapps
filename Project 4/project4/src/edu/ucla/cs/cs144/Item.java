package edu.ucla.cs.cs144;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

public class Item {

	public Item(long id, String nm, String desc, BigDecimal sp, BigDecimal bp, Date st, Date et, User s, Set<String> c) {
		itemId = id;
		name = nm;
		description = desc;
		startPrice = sp;
		buyPrice = bp;
		startTime = st;
		endTime = et;
		seller = s;
		categories = c;
	}

	public long getId() { return itemId; }
	public String getName() { return name; }
	public String getDescription() { return description; }
	public BigDecimal getStartPrice() { return startPrice; }
	public BigDecimal getBuyPrice() { return buyPrice; }
	public Date getStartTime() { return startTime; }
	public Date getEndTime() { return endTime; }
	public User getSeller() { return seller; }
	public Set<String> getCategories() { return categories; }

	private long itemId;
	private String name;
	private String description;
	private BigDecimal startPrice;
	private BigDecimal buyPrice;
	private Date startTime;
	private Date endTime;
	private User seller;
	private Set<String> categories;
}