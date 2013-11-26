<%@ page import="edu.ucla.cs.cs144.*" %>
<html>

<head>
	<link rel="stylesheet" type="text/css" href="css/main.css">
	<link rel="stylesheet" type="text/css" href="css/bootstrap.css">
	<title> Order Confirmation </title>
</head>

<body>
	<div class="container">
		<div class="row">
		<% if ((request.getAttribute("valid").equals("true")) && (request.getAttribute("secure").equals("true"))) { %>
			<% Item item = (Item) session.getAttribute("item"); %>
			<div class="jumbotron"> 
				<h1 class="text-center"> Order Confirmation on Item <%= item.getId() %> </h1>
			</div>

			<table class="table table-bordered table-hover">
				<tr>
					<th> Item Name </th>
					<td> <%= item.getName() %> </td>	
				</tr>
				<tr>
					<th> Description </th>
					<td> <%= item.getDescription() %> </td>	
				</tr>
				<tr>
					<th> Purchase Amount </th>
					<td> <%= item.getBuyPrice() %> </td>	
				</tr>
				<tr>
					<th> Purchase Time </th>
					<td> <%= session.getAttribute("card-number") %> </td>	
				</tr>
				<tr>
					<th> Purchase Time </th>
					<td> <%= session.getAttribute("purchase-time") %> </td>	
				</tr>
			</table>
		<% } else { %>
			<div class="jumbotron">
				<h1 class="text-center"> Unable to process order </h1>
				<div class="alert alert-danger"> <p> Session is not valid or not secure </div>
			</div>
		<% } %>
		</div>
	</div>
</body>

</html>