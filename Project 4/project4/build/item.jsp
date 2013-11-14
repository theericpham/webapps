<%@ page import="package edu.ucla.cs.cs144.*;" %>

<html>

<head> <title> Item Servlet Response </title> </head>

<body>

<%= Item item = (Item) request.getAttribute("item") %>

<h1> Name: <%= request.getAttribute("name") %> </h1>
<h1> Buy For: <%= request.getAttribute("buy") %> </h1>
<h1> Minimum Bid: <%= item.getStartPrice() %> </h1>

</body>

</html>