package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Vector;
import java.util.Comparator;
import java.util.Arrays;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;


public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}

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
    static BigDecimal strip(String money) {
        if (money.equals(""))
            return new BigDecimal("0.00");
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
            return new BigDecimal(nf.format(am).substring(1));
        }
    }

    private String[] toStringArray(NodeList list) {
    	int size = list.getLength();
    	String[] res = new String[size];
    	for (int i = 0; i < size; i++) {
    		res[i] = list.item(i).getTextContent();
    	}
    	return res;
    }

    private String toString(String[] arr) {
    	int size = arr.length;
    	String res = "";
    	for (int i = 0; i < size - 1; i++) {
    		res += arr[i] + ", ";
    	}
    	res += arr[size-1];
    	return res;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // Extract ItemID from request and get xml data
        String id = request.getParameter("id");
        String xmlData = AuctionSearchClient.getXMLDataForItemId(id);
        request.setAttribute("xml", xmlData);

        // Create and parse XML DOM from xml data
        try {
        	 if (!xmlData.equals("")) {
        	 	request.setAttribute("found", "yes");
        	 	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        	DocumentBuilder builder = factory.newDocumentBuilder();
	        	Document doc = builder.parse(new InputSource(new StringReader(xmlData)));

	        	Element root = doc.getDocumentElement();

	        	// Item data
	        	String itemId = root.getAttribute("ItemID");
	        	String name = getElementTextByTagNameNR(root, "Name");
				String description = getElementTextByTagNameNR(root, "Description");
				BigDecimal startPrice = strip(getElementTextByTagNameNR(root, "First_Bid"));
				BigDecimal buyPrice = strip(getElementTextByTagNameNR(root, "Buy_Price"));
				BigDecimal curPrice = strip(getElementTextByTagNameNR(root, "Currently"));
				Date startTime = new Date(getElementTextByTagNameNR(root, "Started"));
				Date endTime = new Date(getElementTextByTagNameNR(root, "Ends"));
				Item item = new Item(itemId, name, description, startPrice, buyPrice, curPrice, startTime, endTime);
				request.setAttribute("item", item);

				// Seller data
				Element seller = getElementByTagNameNR(root, "Seller");
				String sellerId = seller.getAttribute("UserID");
				String sellerRating = seller.getAttribute("Rating");
				String location = getElementTextByTagNameNR(root, "Location");
				String country = getElementTextByTagNameNR(root, "Country");
				User u = new User(sellerId, Integer.parseInt(sellerRating), location, country);
				request.setAttribute("seller", u);

				// Bidder data
				Element bidsRoot = getElementByTagNameNR(root, "Bids");
				Element[] bidElements = getElementsByTagNameNR(bidsRoot, "Bid");
				// Bid b = new Bid(new User("Eric", 1000000, "LA", "USA"), new BigDecimal("20.00"), new Date());
				// Bid[] bids = {b};
				Bid[] bids = new Bid[bidElements.length];
				for (int i = 0; i < bidElements.length; i++) {
					Element e = bidElements[i];
					BigDecimal amount = strip(getElementTextByTagNameNR(e, "Amount"));
					Date time = new Date(getElementTextByTagNameNR(e, "Time"));
					Element bidder = getElementByTagNameNR(e, "Bidder");
					String bid = bidder.getAttribute("UserID");
					String rating = bidder.getAttribute("Rating");
					String loc = getElementTextByTagNameNR(bidder, "Location");
					String cou = getElementTextByTagNameNR(bidder, "Country");
					bids[i] = new Bid(new User(bid, Integer.parseInt(rating), loc, cou), amount, time);
					// bids[i] = new Bid(new User("Eric", 1000000, "LA", "USA"), new BigDecimal("20.00"), new Date());
				}
				Arrays.sort(bids, new Comparator<Bid>() {
					public int compare(Bid a, Bid b) {
						return a.getTime().compareTo(b.getTime());
					}
				});
				request.setAttribute("bids", bids);

				// Categories
				String[] categoriesArr = toStringArray(root.getElementsByTagName("Category"));
				String categories = toString(categoriesArr);
				request.setAttribute("categories", categories); 
       		} else {
       			request.setAttribute("found", "no");
        	}
        } catch (SAXException e) {
        	System.out.println("SAX Exception Occurred.");
        	e.printStackTrace();
        } catch (ParserConfigurationException e) {
			System.out.println("Parser Configuration Exception Occurred.");
        	e.printStackTrace();
        } catch (IOException e) {
			System.out.println("I/O Exception Occurred.");
        	e.printStackTrace();
        } catch(Exception e) {
        	System.out.println("Some Exception Occurred.");
        	e.printStackTrace();
        } finally {
        	request.getRequestDispatcher("/item.jsp").forward(request, response);
        }
    }
}
