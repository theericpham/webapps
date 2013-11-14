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


public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}

     private String getElementTextByTagName(Element parent, String tag) {
    	NodeList l = parent.getElementsByTagName(tag);
    	return (l.getLength() > 0) ? l.item(0).getTextContent() : "";

    }

    private BigDecimal strip(String money) {
    	return (money.equals("")) ? new BigDecimal("0.00") : new BigDecimal(money.substring(1));
    }

    private String[] toStringArray(NodeList list) {
    	int size = list.getLength();
    	String[] res = new String[size];
    	for (int i = 0; i < size; i++) {
    		res[i] = list.item(i).getTextContent();
    	}
    	return res;
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // Extract ItemID from request and get xml data
        String id = request.getParameter("id");
        String xmlData = AuctionSearchClient.getXMLDataForItemId(id);

        // Create and parse XML DOM from xml data
        try {
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder builder = factory.newDocumentBuilder();
        	Document doc = builder.parse(new InputSource(new StringReader(xmlData)));

        	Element root = doc.getDocumentElement();

        	// Item data
        	String itemId = root.getAttribute("ItemID");
        	String name = getElementTextByTagName(root, "Name");
			String description = getElementTextByTagName(root, "Description");
			BigDecimal startPrice = strip(getElementTextByTagName(root, "First_Bid"));
			BigDecimal buyPrice = strip(getElementTextByTagName(root, "Buy_Price"));
			Date startTime = new Date(getElementTextByTagName(root, "Started"));
			Date endTime = new Date(getElementTextByTagName(root, "Ends"));

			// Seller data
			// Node seller = root.getElementsByTagName("Seller").item(0);
			// String sellerId = seller.getAttribute("UserID");
			// String sellerRating = seller.getAttribute("Rating");
			String location = getElementTextByTagName(root, "Location");
			String country = getElementTextByTagName(root, "Country");

			// Categories

			String[] categories = toStringArray(root.getElementsByTagName("Category"));

			request.setAttribute("itemId", itemId);
			request.setAttribute("name", name);
			request.setAttribute("description", description);
			request.setAttribute("startPrice", startPrice);
			request.setAttribute("buyPrice", buyPrice);
			request.setAttribute("startTime", startTime);
			request.setAttribute("endTime", endTime);
			// request.setAttribute("categories", categories);
			// request.setAttribute("sellerId", sellerId);
			// request.setAttribute("sellerRating", sellerRating);
			request.setAttribute("location", location);
			request.setAttribute("country", country);
	        request.getRequestDispatcher("/item.jsp").forward(request, response);
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
        } 

        // // test
        // response.setContentType("text/html");
        // PrintWriter out = response.getWriter();

        // out.println("<html> <head> <title> Item Servlet Response </title> </head>");
        // out.println("<body>");
        // out.println(xmlData);
        // out.println("</body>");
        // out.println("</html>");

        // out.close();
    }
}
