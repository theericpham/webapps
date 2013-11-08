package edu.ucla.cs.cs144;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.* ;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import java.util.Date;
import java.util.Iterator;
import java.text.SimpleDateFormat;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchConstraint;
import edu.ucla.cs.cs144.SearchResult;
import edu.ucla.cs.cs144.Bid;

import java.math.BigDecimal;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/*
 *  Libraries to help with XML creation
 */
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class AuctionSearch implements IAuctionSearch {

	/* 
         * You will probably have to use JDBC to access MySQL data
         * Lucene IndexSearcher class to lookup Lucene index.
         * Read the corresponding tutorial to learn about how to use these.
         *
         * Your code will need to reference the directory which contains your
	 * Lucene index files.  Make sure to read the environment variable 
         * $LUCENE_INDEX with System.getenv() to build the appropriate path.
	 *
	 * You may create helper functions or classes to simplify writing these
	 * methods. Make sure that your helper functions are not public,
         * so that they are not exposed to outside of this class.
         *
         * Any new classes that you create should be part of
         * edu.ucla.cs.cs144 package and their source files should be
         * placed at src/edu/ucla/cs/cs144.
         *
         */
	public AuctionSearch() {
		indexDirectory = System.getenv("LUCENE_INDEX");
	}
	
	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn) {
		// TODO: Your code here!
		try {
			searcher = new IndexSearcher(indexDirectory);
			parser = new QueryParser("content", new StandardAnalyzer());

			Query q = parser.parse(query);
			Hits hits = searcher.search(q);

			SearchResult[] results = new SearchResult[numResultsToReturn];
			Iterator<Hit> iter = hits.iterator();
			int ix = 0;
			int skipped = 0;
			Document d;
			Hit h;
			while (iter.hasNext() && (ix != numResultsToReturn)) {
				if (skipped != numResultsToSkip) {
					iter.next();
					skipped++;
					continue;
				}
				h = iter.next();
				d = h.getDocument();
				results[ix] = new SearchResult(d.get("id"), d.get("name"));
				ix++;
			}
			return results;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new SearchResult[0];
	}

	public SearchResult[] advancedSearch(SearchConstraint[] constraints, 
			int numResultsToSkip, int numResultsToReturn) {

		//query strings		
		String sql_query = "SELECT DISTINCT Auctions.ItemId, Name FROM Auctions LEFT JOIN Bids ON Auctions.ItemId=Bids.ItemId WHERE 1";
		String lucene_ItemName_query ="";
		String lucene_Category_query ="";
		String lucene_Description_query ="";

		//variables to store search contraints
		String fieldName;
		String value;
		
		//Iterate through the SearchConstraint array in order to create the query strings
		for (int i=0; i<constraints.length; i++) {
			fieldName = constraints[i].getFieldName();
			value = constraints[i].getValue();
			if (fieldName=="ItemName") {
				lucene_ItemName_query+=" "+value;
			}
			else if (fieldName=="Category") {
				lucene_Category_query+=" "+value;
			}
			else if (fieldName=="SellerId") {
				sql_query+=" AND SellerId="+value;
			}
			else if (fieldName=="BuyPrice") {
				sql_query+=" AND BuyPrice="+value;
			}
			else if (fieldName=="Bidder") {
				sql_query+=" AND BidderId="+value;
			}
			else if (fieldName=="EndTime") {
				sql_query+=" AND EndTime="+value;
			}
			else if (fieldName=="Description") {
				lucene_Description_query+=" "+value;
			}
		}

		//ArrayLists to hold the results
		ArrayList<SearchResult> mysql_results = new ArrayList<SearchResult>();
		ArrayList<SearchResult> lucene_results = new ArrayList<SearchResult>();
		ArrayList<SearchResult> combined_results = new ArrayList<SearchResult>();

		//run SQL query
		try {
			Class.forName("com.mysql.jdbc.Driver"); 
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS144", "cs144", ""); 
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(sql_query);
			String itemId, name;
			while(rs.next()) {
				SearchResult sr = new SearchResult(rs.getString("ItemId"),rs.getString("Name"));
				mysql_results.add(sr);
			}
		}
		catch (ClassNotFoundException ex) {
		            System.out.println(ex);
	       	} 
	       	catch (SQLException ex) {
		            System.out.println("SQLException caught");
		            System.out.println("---");
		            while ( ex != null ){
		                System.out.println("Message   : " + ex.getMessage());
		                System.out.println("SQLState  : " + ex.getSQLState());
		                System.out.println("ErrorCode : " + ex.getErrorCode());
		                System.out.println("---");
		                ex = ex.getNextException();
	            		}
	       	}

		//run Lucene queries and store the results in lucene_results
		//TO-DO
	       	//NOTE TO ERIC:
	       	//Can you write some code that exucutes Lucene queries
	       	//I already generated the query strings which are name lucene_ItemName_query,
	       	//lucene_Category_query, and lucene_Description_query.
	       	//The results should be stored in the combined_results ArrayList which I defined above

	    /*
			Eric's Notes:
			try {
				searcher = new IndexSearcher(indexDirectory);
				parser = new QueryParser(NameOfIndex, new StandardAnalyzer());
				Query itemNamesQuery = parser.parse(lucene_ItemName_query);
				Hits itemNamesHits = searcher.search(itemsNamesQuery);

				Pseudocode:
				Iterate through itemNamesHits and process
			}
			catch (...) { ... }

	    */

		//find intersection of result arrays. match on item_id
		for(SearchResult mysql_sr : mysql_results) {
			String item_id = mysql_sr.getItemId();
			for (SearchResult lucene_sr : lucene_results) {
				if (item_id == lucene_sr.getItemId()) {
					combined_results.add(mysql_sr);
					break;
				}
			}
		}

		//array that is eventually returned
		SearchResult[] results = new SearchResult[numResultsToReturn];

		//populate the results array
		int num_results_added = 0;
		for (int i=numResultsToSkip; i<combined_results.size(); i++) {
			if (numResultsToReturn != 0) {
				if (num_results_added >= numResultsToReturn) break;
			}
			results[i-numResultsToSkip] = combined_results.get(i);
			num_results_added++;
		}

		return results;
	}

	public String getXMLDataForItemId(String itemId) {
		String XMLData = "";

		try {
			// Init DB Connection and Retrieve Item from Auctions
			Connection conn = DbManager.getConnection(true);
			PreparedStatement prepareItemSelect = conn.prepareStatement(
				"SELECT * FROM Auctions WHERE ItemID = ?"
			);
			long id = Long.parseLong(itemId);
			prepareItemSelect.setLong(1, id);
			ResultSet item = prepareItemSelect.executeQuery();

			while (item.next()) {
				// Auctions Fields      
				String name       	   = item.getString("Name");
				String desc       	   = item.getString("Description");
				String sellerID   	   = item.getString("SellerID");
				Timestamp startTime    = item.getTimestamp("StartTime");
				Timestamp endTime 	   = item.getTimestamp("EndTime");
				BigDecimal buyPrice   	   = item.getBigDecimal("BuyPrice");
				BigDecimal startPrice  	   = item.getBigDecimal("StartPrice");

				// Seller Information
				PreparedStatement prepareSellerSelect = conn.prepareStatement(
					"SELECT * FROM Users WHERE UserID = ?"
				);
				prepareSellerSelect.setString(1, sellerID);
				ResultSet seller = prepareSellerSelect.executeQuery();
				int rating = 0;
				String location = "";
				String country  = "";
				if (seller.next()) {
					rating   = seller.getInt("Rating");
					location = seller.getString("Location");
					country  = seller.getString("Country");
				}

				// Categories
				Set<String> categories = new HashSet<String>();
				PreparedStatement prepareCategoriesSelect = conn.prepareStatement(
					"SELECT Name FROM AuctionsCategories JOIN Categories ON AuctionsCategories.CategoryID = Categories.CategoryID WHERE ItemID = ?"
				);
				prepareCategoriesSelect.setLong(1, id);
				ResultSet categoryRS = prepareCategoriesSelect.executeQuery();
				while (categoryRS.next()) {
					categories.add(categoryRS.getString("Name"));
				}

				// Bids
				List<Bid> bids = new ArrayList<Bid>();
				PreparedStatement prepareBidsSelect = conn.prepareStatement(
					"SELECT * FROM Bids JOIN Users on Bids.BidderID = Users.UserID WHERE ItemID = ? ORDER BY TIME ASC"
				);
				prepareBidsSelect.setLong(1, id);
				ResultSet bidRS = prepareBidsSelect.executeQuery();
				while (bidRS.next()) {
					bids.add(
						new Bid(
							id, 
							bidRS.getString("BidderID"), 
							bidRS.getTimestamp("Time"), 
							bidRS.getBigDecimal("Amount"), 
							bidRS.getInt("Rating"),
							bidRS.getString("Location"),
							bidRS.getString("Country")
						)
					);
				}

			// 	/*
			// 	 *  Build XML Structure
			// 	 */
				DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
				DocumentBuilder b          = fac.newDocumentBuilder();
				org.w3c.dom.Document doc   = b.newDocument();

				// root element
				Element root = doc.createElement("Item");
				root.setAttribute("ItemID", itemId);
				doc.appendChild(root);

				// name element
				Element nameElem = doc.createElement("Name");
				nameElem.appendChild(doc.createTextNode(name));
				root.appendChild(nameElem);

				// category elements
				Element catElem;
				for (String c : categories) {
					catElem = doc.createElement("Category");
					catElem.appendChild(doc.createTextNode(c));
					root.appendChild(catElem);
				}

				// currently element
				BigDecimal currentPrice = (bids.size()) > 0 ? bids.get(bids.size()-1).getAmount() : startPrice;
				Element currElem = doc.createElement("Currently");
				currElem.appendChild(doc.createTextNode("$" + currentPrice.toString()));
				root.appendChild(currElem);

				// buy 
				if (buyPrice.doubleValue() > 0) {
					Element buyElem = doc.createElement("Buy_Price");
					buyElem.appendChild(doc.createTextNode("$" + buyPrice.toString()));
					root.appendChild(buyElem);
				}

				// start
				Element startElem = doc.createElement("First_Bid");
				startElem.appendChild(doc.createTextNode("$" + startPrice.toString()));
				root.appendChild(startElem);

				// num bids
				Element numBidsElem = doc.createElement("Number_of_Bids");
				numBidsElem.appendChild(doc.createTextNode(Integer.toString(bids.size())));
				root.appendChild(numBidsElem);

				// bids
				Element bidsElem = doc.createElement("Bids");
				Element bidElem;
				for (Bid bid : bids) {
					bidElem = doc.createElement("Bid");

					// bidder
					Element bidderElem = doc.createElement("Bidder");
					bidderElem.setAttribute("UserID", bid.getBidderID());
					bidderElem.setAttribute("Rating", Integer.toString(bid.getBidderRating()));
					if (!bid.getBidderLocation().equals("")) {
						Element locElem = doc.createElement("Location");
						locElem.appendChild(doc.createTextNode(bid.getBidderLocation()));
						bidderElem.appendChild(locElem);
					}
					if (!bid.getBidderCountry().equals("")) {
						Element countryElem = doc.createElement("Country");
						countryElem.appendChild(doc.createTextNode(bid.getBidderCountry()));
						bidderElem.appendChild(countryElem);
					}
					bidElem.appendChild(bidderElem);

					// time
					Element timeElem = doc.createElement("Time");
					String t = new SimpleDateFormat("MMM-dd-yy HH:mm:ss").format(bid.getTime());
					timeElem.appendChild(doc.createTextNode(t));
					bidElem.appendChild(timeElem);

					// amount
					Element amountElem = doc.createElement("Amount"); 
					amountElem.appendChild(doc.createTextNode("$" + bid.getAmount().toString()));
					bidElem.appendChild(amountElem);

					bidsElem.appendChild(bidElem);
				}
				root.appendChild(bidsElem);

				// location
				Element locElem = doc.createElement("Location");
				locElem.appendChild(doc.createTextNode(location));
				root.appendChild(locElem);

				// country
				Element countryElem = doc.createElement("Country");
				countryElem.appendChild(doc.createTextNode(country));
				root.appendChild(countryElem);

				// started
				Element startedElem = doc.createElement("Started");
				startedElem.appendChild(doc.createTextNode(new SimpleDateFormat("MMM-dd-yy HH:mm:ss").format(startTime)));
				root.appendChild(startedElem);

				// ends
				Element endsElem = doc.createElement("Ends");
				endsElem.appendChild(doc.createTextNode(new SimpleDateFormat("MMM-dd-yy HH:mm:ss").format(endTime)));
				root.appendChild(endsElem);

				// seller
				Element sellerElem = doc.createElement("Seller");
				sellerElem.setAttribute("Rating", Integer.toString(rating));
				sellerElem.setAttribute("UserID", sellerID);
				root.appendChild(sellerElem);

				// description
				Element descElem = doc.createElement("Description");
				descElem.appendChild(doc.createTextNode(desc));
				root.appendChild(descElem);

				TransformerFactory tfac = TransformerFactory.newInstance();
				Transformer transformer = tfac.newTransformer();
				DOMSource source = new DOMSource(doc);
				StringWriter w = new StringWriter();
				StreamResult r = new StreamResult(w);
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				transformer.transform(source, r);
				XMLData = w.toString();
			}

		} catch (SQLException e) {
			System.out.println("SQL Error.");
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			System.out.println("Parser Configuration Error.");
		} catch (TransformerException e) {
			System.out.println("Transformer Error.");
		}

		return XMLData;
	}
	
	public String echo(String message) {
		return message;
	}

	private String indexDirectory;
	private IndexSearcher searcher;
	private QueryParser parser;
}
