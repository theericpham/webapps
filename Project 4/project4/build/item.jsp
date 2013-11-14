<html>

<head> <title> Item Servlet Response </title> </head>

<body>

<h1> This is our JSP </h1>

<%= request.getAttribute("xmlData") %>

<h2> ItemId: <%= request.getAttribute("id") %> </h2>
<h2> Root Tag: <%= request.getAttribute("rootTag") %> </h2>


</body>

</html>