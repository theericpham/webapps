<%@ page import="edu.ucla.cs.cs144.*" %>
<html>
    <head>
        <title>Keyword Search</title>
        <link rel="stylesheet" type="text/css" href="css/main.css">
        <script src="js/jquery.js"></script>
        <script src="js/main.js"></script>
    </head>
    <body>
        <h1>Keyword Search</h1>
        <form action="search" method="GET">
            <input type="text" id="keyword_input" placeholder="Keyword Search" name="q" />
            <div id="suggestions">

            </div>
            <input type="hidden" name="numResultsToSkip" value="0"/>
            <input type="hidden" name="numResultsToReturn" value="20" />
            <input type="submit" value="Search" /><br/>
        </form>
        <h3>Search Results matching query: <%= request.getAttribute("query") %></h3>
        <%
            SearchResult[] sr = (SearchResult[])request.getAttribute("search_results");
            int numResultsToSkip = Integer.parseInt(request.getParameter("numResultsToSkip"));
            int numResultsToReturn = Integer.parseInt(request.getParameter("numResultsToReturn"));
            int nextIndex = numResultsToSkip + sr.length;
            int previousIndex = numResultsToSkip - numResultsToReturn;
            if (previousIndex < 0) previousIndex = 0;

            for (int i=0; i<sr.length; i++) { %>
                <a href="item?id=<%= sr[i].getItemId() %>"><%= sr[i].getName() %></a><br/>
            <% } %>
            <br/>
            <%
            if (numResultsToSkip > 0) { %>
                <form action="search" method="GET">
                    <input type="hidden" name="q" value="<%= request.getAttribute("query") %>"/>
                    <input type="hidden" name="numResultsToSkip" value="<%= previousIndex %>"/>
                    <input type="hidden" name="numResultsToReturn" value="20" />
                    <input type="submit" value="< Previous" />
                </form> 
            <% }
            if (sr.length == numResultsToReturn) { %>
                <form action="search" method="GET">
                    <input type="hidden" name="q" value="<%= request.getAttribute("query") %>"/>
                    <input type="hidden" name="numResultsToSkip" value="<%= nextIndex %>"/>
                    <input type="hidden" name="numResultsToReturn" value="20" />
                    <input type="submit" value="Next >" /><br/>
                </form>    
            <% }
        %>
    </body>
</html>