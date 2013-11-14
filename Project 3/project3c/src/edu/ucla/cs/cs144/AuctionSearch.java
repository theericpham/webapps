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
import edu.ucla.cs.cs144.FieldName;

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
	
	private ArrayList<SearchResult> intersection(ArrayList<SearchResult> l1, ArrayList<SearchResult> l2) {
		ArrayList<SearchResult> result = new ArrayList<SearchResult>();
		for (SearchResult sr1 : l1) {
			String item_id = sr1.getItemId();
			for (SearchResult sr2 : l2) {
				if (item_id.equals(sr2.getItemId())) {
					result.add(sr1);
					break;
				}
			}
		}
		return result;
	}

	public AuctionSearch() {
		indexDirectory = System.getenv("LUCENE_INDEX");
	}
	
	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn) {
		// TODO: Your code here!
		try {
			searcher = new IndexSearcher(indexDirectory);
			parser = new QueryParser("content", new StandardAnalyzer());

			Query q = parser.parse(QueryParser.escape(query));
			Hits hits = searcher.search(q);

			int num_possible = hits.length() - numResultsToSkip;
			if (num_possible < 1) return new SearchResult[0];
			if (numResultsToReturn == 0) numResultsToReturn = num_possible;
			if (numResultsToReturn > num_possible) numResultsToReturn = num_possible;

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

		//query strings and flags		
		String sql_query = "SELECT DISTINCT Auctions.ItemId, Name FROM Auctions LEFT JOIN Bids ON Auctions.ItemId=Bids.ItemId WHERE 1";
		boolean uses_sql = false;
		String lucene_ItemName_query ="";
		boolean uses_lucene_name = false;
		String lucene_Category_query ="";
		boolean uses_lucene_category = false;
		String lucene_Description_query ="";
		boolean uses_lucene_description = false;

		//variables to store search contraints
		String fieldName;
		String value;
		
		//Iterate through the SearchConstraint array in order to create the query strings
		for (int i=0; i<constraints.length; i++) {
			fieldName = constraints[i].getFieldName();
			value = constraints[i].getValue();
			if (fieldName.equals(FieldName.ItemName)) {
				lucene_ItemName_query+=" "+value;
				uses_lucene_name = true;
			}
			else if (fieldName.equals(FieldName.Category)) {
				lucene_Category_query+=" "+value;
				uses_lucene_category = true;
			}
			else if (fieldName.equals(FieldName.SellerId)) {
				sql_query+=" AND SellerID=\""+value+"\"";
				uses_sql = true;
			}
			else if (fieldName.equals(FieldName.BuyPrice)) {
				sql_query+=" AND BuyPrice="+value;
				uses_sql = true;
			}
			else if (fieldName.equals(FieldName.BidderId)) {
				sql_query+=" AND BidderID=\""+value+"\"";
				uses_sql = true;
			}
			else if (fieldName.equals(FieldName.EndTime)) {
				try {
				 	Date d = new SimpleDateFormat("MMM-dd-yy HH:mm:ss").parse(value);
				 	String timestamp_string= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
					sql_query+=" AND EndTime=\""+timestamp_string+"\"";
					uses_sql = true;
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (fieldName.equals(FieldName.Description)) {
				lucene_Description_query+=" "+value;
				uses_lucene_description = true;
			}
		}

		//ArrayLists to hold the results
		ArrayList<SearchResult> mysql_results = new ArrayList<SearchResult>();
		ArrayList<SearchResult> lucene_name_results = new ArrayList<SearchResult>();
		ArrayList<SearchResult> lucene_category_results = new ArrayList<SearchResult>();
		ArrayList<SearchResult> lucene_description_results = new ArrayList<SearchResult>();
		ArrayList<SearchResult> combined_results = new ArrayList<SearchResult>();

		//run SQL query
		if (uses_sql) {
			try {
				Class.forName("com.mysql.jdbc.Driver"); 
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS144", "cs144", ""); 
				Statement s = con.createStatement();
				ResultSet rs = s.executeQuery(sql_query);
				String itemId, name;
				while(rs.next()) {
					mysql_results.add(new SearchResult(rs.getString("ItemId"),rs.getString("Name")));
				}
				rs.close();
				s.close();
				con.close();
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
		}

		//run lucene ItemName query
		if (uses_lucene_name) {
			try {
				searcher = new IndexSearcher(indexDirectory);
				parser = new QueryParser("name", new StandardAnalyzer());

				Query q = parser.parse(QueryParser.escape(lucene_ItemName_query));
				Hits hits = searcher.search(q);

				SearchResult[] results = new SearchResult[numResultsToReturn];
				Iterator<Hit> iter = hits.iterator();
				Document d;
				Hit h;
				while (iter.hasNext()) {
					h = iter.next();
					d = h.getDocument();
					lucene_name_results.add(new SearchResult(d.get("id"), d.get("name")));
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		//run lucene Description query
		if (uses_lucene_description) {
			try {
				searcher = new IndexSearcher(indexDirectory);
				parser = new QueryParser("description", new StandardAnalyzer());

				Query q = parser.parse(QueryParser.escape(lucene_Description_query));
				Hits hits = searcher.search(q);

				SearchResult[] results = new SearchResult[numResultsToReturn];
				Iterator<Hit> iter = hits.iterator();
				Document d;
				Hit h;
				while (iter.hasNext()) {
					h = iter.next();
					d = h.getDocument();
					lucene_description_results.add(new SearchResult(d.get("id"), d.get("name")));
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		//run lucene Category query
		if (uses_lucene_category) {
			try {
				searcher = new IndexSearcher(indexDirectory);
				parser = new QueryParser("category", new StandardAnalyzer());

				Query q = parser.parse(QueryParser.escape(lucene_Category_query));
				Hits hits = searcher.search(q);

				SearchResult[] results = new SearchResult[numResultsToReturn];
				Iterator<Hit> iter = hits.iterator();
				Document d;
				Hit h;
				while (iter.hasNext()) {
					h = iter.next();
					d = h.getDocument();
					lucene_category_results.add(new SearchResult(d.get("id"), d.get("name")));
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	       	

		ArrayList<SearchResult> combined_part1 = new ArrayList<SearchResult>();
		if (uses_sql) {
			if (uses_lucene_name) combined_part1 = intersection(mysql_results,lucene_name_results);
			else combined_part1 = mysql_results;
		}
		else combined_part1 = lucene_name_results;

		ArrayList<SearchResult> combined_part2 = new ArrayList<SearchResult>();
		if (uses_lucene_category) {
			if (uses_lucene_description) combined_part2 = intersection(lucene_category_results,lucene_description_results);
			else combined_part2 = lucene_category_results;
		}
		else combined_part2 = lucene_description_results;

		if ((uses_sql || uses_lucene_name) && (uses_lucene_description || uses_lucene_category)) {
			combined_results = intersection(combined_part1,combined_part2);
		}
		else if (uses_sql || uses_lucene_name) {
			combined_results = combined_part1;
		}
		else {
			combined_results = combined_part2;
		}

		//create a results array of the appropriate size
		int num_possible = combined_results.size() - numResultsToSkip;
		if (num_possible < 1) return new SearchResult[0];
		if (numResultsToReturn == 0) numResultsToReturn = num_possible;
		if (numResultsToReturn > num_possible) numResultsToReturn = num_possible;

		SearchResult[] results = new SearchResult[numResultsToReturn];

		//populate the results array
		int num_results_added = 0;
		for (int i=numResultsToSkip; i<combined_results.size(); i++) {
			if (num_results_added >= numResultsToReturn) break;
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
