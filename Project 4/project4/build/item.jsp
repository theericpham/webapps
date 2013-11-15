<%@ page import="edu.ucla.cs.cs144.*" %>
<% Item item = (Item) request.getAttribute("item"); %>
<% User seller = (User) request.getAttribute("seller"); %>
<% String categories = (String) request.getAttribute("categories"); %>
<% Bid[] bids = (Bid[]) request.getAttribute("bids"); %>

<html>

<head> <title> Item Servlet Response </title> </head>

<body>

<% if (request.getAttribute("found").equals("yes")) { %>

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

	<% if (bids.length == 1) { %>
	<h1> This item has 1 bid </h1>
	<% } else { %>
	<h1> This item has <%= bids.length %> bids </h1>
	<% } %>
	<% if (bids.length != 0) { %>
	<table>
		<thead>
			<tr> 
				<th> Bidder </th>
				<th> Bidder Rating </th>	
				<th> Bidder Location </th>
				<th> Bid Time </th>
				<th> Bid Amount </th>
			</tr>
		<thead>
		<tbody>
			<% for (int i = 0; i < bids.length; i++) { %>
			<tr>
				<td> <%= bids[i].getBidder().getId() %> </td>
				<td> <%= bids[i].getBidder().getRating() %> </td>
				<td> <%= bids[i].getBidder().getLocationCountry() %> </td>
				<td> <%= bids[i].getTime() %> </td>
				<td> <%= bids[i].getAmount() %> </td>
			</tr>
			<% } %>
		</tbody>
	</table>
	<% } %>

<% } else { %>

	<h1> Item Not Found </h1>

<% } %>

</body>

</html>