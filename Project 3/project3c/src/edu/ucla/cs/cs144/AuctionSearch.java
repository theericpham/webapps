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
		// TODO: Your code here!
		return null;
	}
	
	public String echo(String message) {
		return message;
	}

	private String indexDirectory;
	private IndexSearcher searcher;
	private QueryParser parser;
}
