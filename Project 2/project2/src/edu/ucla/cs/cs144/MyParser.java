/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;


class MyParser {
    
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;

    // File Writers
    private static BufferedWriter usersDAT;
    private static BufferedWriter itemsDAT;
    private static BufferedWriter categoriesDAT;
    private static BufferedWriter itemsCategoriesDAT;
    private static BufferedWriter bidsDAT;

    // Users
    private static Set<String> users;
    private static HashMap<String, String> ratings;
    private static HashMap<String, String> locations;
    private static HashMap<String, String> countries;

    // Categories
    private static HashMap<String, String> categories;
    private static int nCat;

    // AuctionsCategories
    private static HashMap<String, Set<String>> ic;

    static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
    };
    
    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
    }
    
    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }
    
    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);
        
        /* Fill in code here (you will probably need to write auxiliary
            methods). */
        
        Element root 	= doc.getDocumentElement();
        Element items[] = getElementsByTagNameNR(root, "Item");

        for (Element e : items) {
        	parseItem(e);
        }
        
        /**************************************************************/
        
    }

    // Parse through an Item element and collect relevant info.
    static void parseItem(Element item) {
    	// Item Information
    	String itemID 	   = item.getAttribute("ItemID");
    	String itemName    = getElementTextByTagNameNR(item, "Name");
    	String startTime   = formatDate(getElementTextByTagNameNR(item, "Started"));
        String endTime 	   = formatDate(getElementTextByTagNameNR(item, "Ends"));
    	String startPrice  = strip(getElementTextByTagNameNR(item, "First_Bid"));
        String buyPrice    = strip(getElementTextByTagNameNR(item, "Buy_Price"));
        String description = getElementTextByTagNameNR(item, "Description");
        if (description.length() > 4000) description = description.substring(0, 4000);

    	// Seller Information
    	Element seller = getElementByTagNameNR(item, "Seller");
        String sID = seller.getAttribute("UserID");
        String sRating = seller.getAttribute("Rating");
        String sLoc = getElementTextByTagNameNR(item, "Location");
        String sCountry = getElementTextByTagNameNR(item, "Country");
        addUser(sID, sRating, sLoc, sCountry);

        // Parse categories
        Element[] categories = getElementsByTagNameNR(item, "Category");
        parseCategories(categories, itemID);

        // Parse bids
        Element bids = getElementByTagNameNR(item, "Bids");
        parseBids(bids, itemID);

        // Write an entry into Items
        try {
        	String[] itemEntry = { itemID, sID, itemName, startTime, endTime, startPrice, buyPrice, description };
        	writeRecord(itemsDAT, itemEntry);
        }
        catch (IOException e) {
        	System.out.println("Error writing Items entry.");
        }
    }

    /*
     *  For each category associated with itemID,
     *  first add the category to the set of all categories,
     *  then retrieve the categoryID and associate it with itemID
     */
    static void parseCategories(Element[] categories, String itemID) {
    	for (Element e : categories) {
    		addIC(itemID, addCategory(getElementText(e)));
    	}
    }

    // Parse bids for bid info and user info
    static void parseBids(Element root, String itemID) {
    	Element[] bids = getElementsByTagNameNR(root, "Bid");

    	for (Element e : bids) {
    		// Bidder Information
    		Element bidder   = getElementByTagNameNR(e, "Bidder");
    		String  bID      = bidder.getAttribute("UserID");
            String  rating   = bidder.getAttribute("Rating");
            String  location = getElementTextByTagNameNR(bidder, "Location");
            String  country  = getElementTextByTagNameNR(bidder, "Country");
            addUser(bID, rating, location, country);

            // Bid Information
            String time   = formatDate(getElementTextByTagNameNR(e, "Time"));
            String amount = strip(getElementTextByTagNameNR(e, "Amount"));
            try {
            	String[] bidEntry = { itemID, bID, time, amount };
            	writeRecord(bidsDAT, bidEntry);
            }
            catch (IOException ex) {
            	System.out.println("Error writing Bids Entry");
            }
    	}
    }

    // Add user info to hashes.
    static void addUser(String id, String rating, String loc, String country) {
    	users.add(id);
    	ratings.put(id, rating);
    	locations.put(id, loc);
    	countries.put(id, country);
    }

    // Add category to hash.
    static String addCategory(String category) {
    	String cid;
    	if (!categories.containsKey(category)) {
    		cid = String.valueOf(nCat++);
    		categories.put(category, String.valueOf(cid));
    	}
    	else cid = categories.get(category);
    	return cid;
    }

    // Add auction category to hash.
    static void addIC(String itemID, String catID) {
    	Set<String> cids;
    	if (!ic.containsKey(itemID)) cids = new HashSet<String>();
		else cids = ic.get(itemID);

		cids.add(catID);
		ic.put(itemID, cids);
    }

    // Format a date.
    static String formatDate(String in)
    {
        Date d = null;
        try {
            d = new SimpleDateFormat("MMM-dd-yy HH:mm:ss").parse(in);
        } 
        catch (ParseException e) {
            System.out.println("Error parsing date.");
        }
        String out = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
    	return out;
    }

    // Write a general record.
    static void writeRecord(BufferedWriter writer, String[] s) throws IOException {
        int n = s.length;
        String record = "";

        for (int i = 0; i < n-1; i++) {
            record += s[i] + columnSeparator;
        }
        record += s[n-1];

        writer.write(record);
        writer.newLine();
    }

    // Write a user record by gather data from hashes.
    static void writeUsers() {
    	Iterator<String> it = users.iterator();
    	String[] entry = new String[4];
    	while (it.hasNext()) {
    		String id 	   = it.next();
    		String rating  = ratings.get(id);
    		String loc 	   = locations.get(id);
    		String country = countries.get(id);
    		entry[0] = id;
    		entry[1] = rating;
    		entry[2] = loc;
    		entry[3] = country;
    		try {
    			writeRecord(usersDAT, entry);
    		}
    		catch(IOException e) {
    			System.out.println("Error writing Users record");
    		}
    	}
    }

    // Write a category record by gather data from hashes.
    static void writeCategories() {
    	Set<String> cats  = categories.keySet();
    	String[] catEntry = new String[2];
    	String id;
    	for (String c : cats) {
    		id = categories.get(c);
    		catEntry[0] = id;
    		catEntry[1] = c;

    		try {
    			writeRecord(categoriesDAT, catEntry);
    		}
    		catch (IOException e) {
    			System.out.println("Error writing Categories entry.");
    		}
    	}
    }

    // Write item-category record
    static void writeIC() {
    	Set<String> items   = ic.keySet();
    	String[] entry = new String[2];
    	Set<String> cats;
    	String cid;
    	for (String i : items ) {
    		cats = ic.get(i);
    		for(String c : cats) {
    			entry[0] = i;
    			entry[1] = c;

    			try {
    				writeRecord(itemsCategoriesDAT, entry);
    			}
    			catch (IOException ex) {
    				System.out.println("Error writing IC record.");
    			}
    		}
    	}
    }
    
    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        

        try {
        	// Initialize writers.
        	usersDAT 		   = new BufferedWriter(new FileWriter("users.dat", true));
            itemsDAT 		   = new BufferedWriter(new FileWriter("auctions.dat", true));
            categoriesDAT 	   = new BufferedWriter(new FileWriter("categories.dat", true));
            itemsCategoriesDAT = new BufferedWriter(new FileWriter("auctionscategories.dat", true));
            bidsDAT 		   = new BufferedWriter(new FileWriter("bids.dat", true));

            // Initialize data structures.
            users 	   = new HashSet<String>();
            ratings    = new HashMap<String, String>();
            locations  = new HashMap<String, String>();
            countries  = new HashMap<String, String>();
            categories = new HashMap<String, String>();
            ic 		   = new HashMap<String, Set<String>>();

            nCat = 0;

        	/* Process all files listed on command line. */
	        for (int i = 0; i < args.length; i++) {
	            File currentFile = new File(args[i]);
	            processFile(currentFile);
	        }
	        writeUsers();
	        writeCategories();
	        writeIC();

	        // Close writers
	        usersDAT.close();
            itemsDAT.close();
            categoriesDAT.close();
            itemsCategoriesDAT.close();
            bidsDAT.close();
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
    }
}
