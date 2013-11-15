<%@ page import="edu.ucla.cs.cs144.*" %>
<% Item item = (Item) request.getAttribute("item"); %>
<% User seller = (User) request.getAttribute("seller"); %>
<% String categories = (String) request.getAttribute("categories"); %>

<html>

<head> <title> Item Servlet Response </title> </head>

<body>

<h1> Item Information </h1>

<table>
	<tr>
		<th> Name </th>
		<td> <%= item.getName() %> </td>
	</tr>
	<tr>
		<th> Description </th>
		<td> <%= item.getDescription() %> </td>
	</tr>
	<tr>
		<th> Minimum Bid </th>
		<td> <%= item.getStartPrice() %> </td>
	</tr>
	<tr>
		<th> Buy Price </th>
		<td> <%= item.getBuyPrice() %> </td>
	</tr>
	<tr>
		<th> Current Highest Bid </th>
		<td> <%= item.getCurrentPrice() %> </td>
	</tr>
	<tr>
		<th> Started </th>
		<td> <%= item.getStartTime() %> </td>
	</tr>
	<tr>
		<th> Ends </th>
		<td> <%= item.getEndTime() %> </td>
	</tr>
	<tr>
		<th> Categories </th>
		<td> <%= categories %></td>
	</tr>
</table>


<h1> Seller Information </h1>

<table>
	<tr>
		<th> Name </th>
		<td> <%= seller.getId() %> </td>
	</tr>
	<tr>
		<th> Rating </th>
		<td> <%= seller.getRating() %> </td>
	</tr>
	<tr>
		<th> Location </th>
		<td> <%= seller.getLocationCountry() %> </td>
	</tr>
</table>


</body>

</html>