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


public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}

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
        	String itemId = root.getAttribute("ItemID");
        	String tag = root.getTagName();

        	request.setAttribute("xmlData", xmlData);
	        request.setAttribute("id", itemId);
	        request.setAttribute("rootTag", tag);
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
