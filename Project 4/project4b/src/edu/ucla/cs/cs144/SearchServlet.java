package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

public class SearchServlet extends HttpServlet implements Servlet {
       
    public SearchServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {        
        String query = request.getParameter("q");
        int numResultsToSkip = Integer.parseInt(request.getParameter("numResultsToSkip"));
        int numResultsToReturn = Integer.parseInt(request.getParameter("numResultsToReturn"));

        AuctionSearchClient a = new AuctionSearchClient();
        SearchResult[] sr = a.basicSearch(query, numResultsToSkip, numResultsToReturn);

        request.setAttribute("search_results",sr);
        request.setAttribute("query",query);
        request.getRequestDispatcher("keywordSearch.jsp").forward(request, response);
        
    }
}
