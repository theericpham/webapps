package edu.ucla.cs.cs144;

public class Item {

	/*
	 *  Constructors.
	 */
	public Item() {
		id         = "";
		name       = "";
		desc       = "";
		categories = new String[0];
	}

	public Item(String itemID, String itemName, String itemDesc, String[] itemCats) {
		id         = itemID;
		name       = itemName;
		desc 	   = itemDesc;
		categories = itemCats;
	}

	/*
	 *  Setter methods.
	 */
	public void setID(String newID) {
		id = newID;
	}

	public void setName(String newName) {
		name = newName;
	}

	public void setDesc(String newDesc) {
		desc = newDesc;
	}

	public void setCategories(String[] cats) {
		categories = cats;
	}

	/*
	 *  Getter methods.
	 */
	public String getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public String getCategories() {
		String s = "";
		for (String c : categories) {
			s += c + " ";
		}
		return s;
	}

	private String id;
	private String name;
	private String desc;
	private String[] categories;
}