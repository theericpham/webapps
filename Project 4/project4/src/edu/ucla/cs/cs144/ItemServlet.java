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
			BigDecimal curPrice = strip(getElementTextByTagName(root, "Currently"));
			Date startTime = new Date(getElementTextByTagName(root, "Started"));
			Date endTime = new Date(getElementTextByTagName(root, "Ends"));
			Item item = new Item(itemId, name, description, startPrice, buyPrice, curPrice, startTime, endTime);
			request.setAttribute("item", item);

			// Seller data
			Element seller = (Element) root.getElementsByTagName("Seller").item(0);
			String sellerId = seller.getAttribute("UserID");
			String sellerRating = seller.getAttribute("Rating");
			String location = getElementTextByTagName(root, "Location");
			String country = getElementTextByTagName(root, "Country");
			User u = new User(sellerId, Integer.parseInt(sellerRating), location, country);
			request.setAttribute("seller", u);

			// Categories
			String[] categoriesArr = toStringArray(root.getElementsByTagName("Category"));
			String categories = toString(categoriesArr);
			request.setAttribute("categories", categories);

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
