package edu.ucla.cs.cs144;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;

import java.io.*;
import java.util.*;

public class Indexer {
    
    /** Creates a new instance of Indexer */
    public Indexer() {
        indexDirectory = System.getenv("LUCENE_INDEX") + "/index";
    }

    public IndexWriter getIndexWriter(String dir, boolean create) throws IOException {
        if (indexWriter == null) {
            indexWriter = new IndexWriter(dir, new StandardAnalyzer(), create);
        }
        return indexWriter;
   }    
   
    public void closeIndexWriter() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
   }
 
    public void rebuildIndexes() {

        Connection conn = null;

        // create a connection to the database to retrieve Items from MySQL
        // erase existing index
    	try {
    	    conn = DbManager.getConnection(true);
            getIndexWriter(indexDirectory, true);
    	} catch (SQLException ex) {
    	    System.out.println(ex);
    	} catch (IOException ex) {
            System.out.println(ex);
        }

        // collect all item info from database and index them one by one
        try {
            // select all item-category tuples from database
            String itemsCategoriesQuery        = "SELECT AuctionsCategories.ItemID, Name FROM AuctionsCategories JOIN Categories ON AuctionsCategories.CategoryID = Categories.CategoryID";
            Statement itemsCategoriesStatement = conn.createStatement();
            ResultSet itemsCategoriesRS        = itemsCategoriesStatement.executeQuery(itemsCategoriesQuery);

            // store results for easy retrieval later
            HashMap<Integer, Set<String>> itemsCategories = new HashMap<Integer, Set<String>>(); 
            int id;
            String cat;
            Set<String> s;
            while (itemsCategoriesRS.next()) {
                id  = itemsCategoriesRS.getInt("ItemID");
                cat = itemsCategoriesRS.getString("Name");
                s = itemsCategories.get(id);
                if (s == null) {
                    s = new HashSet<String>();
                }
                s.add(cat);
                itemsCategories.put(id, s);
            }

            // select all items from database
            String itemsQuery        = "SELECT ItemID, Name, Description FROM Auctions";
            Statement itemsStatement = conn.createStatement();
            ResultSet items          = itemsStatement.executeQuery(itemsQuery);

            // create index on all items
            String name;
            String desc;
            String[] cats;
            while (items.next()) {
                id   = items.getInt("ItemID");
                name = items.getString("Name");
                desc = items.getString("Description");
                s    = itemsCategories.get(id);
                cats = s.toArray(new String[s.size()]);
                indexItem(new Item(Integer.toString(id), name, desc, cats));
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }

        // close the database connection
        // close index writer
    	try {
            closeIndexWriter();
    	    conn.close();
    	} catch (SQLException ex) {
    	    System.out.println(ex);
    	} catch (IOException ex) {
            System.out.println(ex);
        }
    }    

    public void indexItem(Item item) throws IOException {
        IndexWriter writer = getIndexWriter(indexDirectory, false);
        Document doc = new Document();
        doc.add(new Field("id", item.getID(), Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("name", item.getName(), Field.Store.YES, Field.Index.TOKENIZED));
        doc.add(new Field("description", item.getDesc(), Field.Store.NO, Field.Index.TOKENIZED));
        doc.add(new Field("category", item.getCategories(), Field.Store.NO, Field.Index.TOKENIZED));
        String fullSearchableText = item.getID() + " " + item.getName() + " " + item.getDesc() + " " +  item.getCategories();
        doc.add(new Field("content", fullSearchableText, Field.Store.NO, Field.Index.TOKENIZED));

        try {
            writer.addDocument(doc);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public static void main(String args[]) {
        Indexer idx = new Indexer();
        idx.rebuildIndexes();
    }   

    private IndexWriter indexWriter;
    private String indexDirectory;
}
