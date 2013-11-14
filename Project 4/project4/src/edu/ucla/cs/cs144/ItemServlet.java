package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;

public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // Extract ItemID from request and get xml data
        String id = request.getParameter("id");
        String xmlData = AuctionSearchClient.getXMLDataForItemId(id);

        request.setAttribute("xmlData", xmlData);
        request.getRequestDispatcher("/item.jsp").forward(request, response);

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
