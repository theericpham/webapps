package edu.ucla.cs.cs144;

import java.io.IOException;
import java.net.HttpURLConnection;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.io.*;
import java.net.*;

public class ProxyServlet extends HttpServlet implements Servlet {
       
    public ProxyServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        PrintWriter out = response.getWriter();
    
        String query = URLEncoder.encode(request.getParameter("q"),"UTF-8");

        String googleAPI = "http://google.com/complete/search?output=toolbar&q="+query;
        
        HttpURLConnection connection = null;
        URL serverAddress = null;
        String google_response = "";

        try {
            serverAddress = new URL(googleAPI);
            connection = (HttpURLConnection)serverAddress.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine())!=null) {
                out.println(inputLine);
            }
            response.setContentType("text/xml");
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
