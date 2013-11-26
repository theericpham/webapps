<%@ page import="edu.ucla.cs.cs144.*" %>
<html>	
	<head>	
		<link rel="stylesheet" type="text/css" href="css/bootstrap.css">
		<title> Order Details </title> 
	</head>	
	<body>
		<div class="container">
			<div class="row">
				<% if (request.getAttribute("valid").equals("false")) { %>
					<div class="jumbotron">
						<h1 class="text-center"> Unable to obtain order details. </h1>
						<div class="alert alert-danger"> <p> Session could not be verified. </div>
					</div>
				<% } else { %>
					<% Item item = (Item) request.getAttribute("item"); %>
					<div class="jumbotron">
						<h1 class="text-center"> Order Details for Item <%= item.getId() %> : </h1>
					</div>
					<form action="">
						<table class="table table-hover">
							<tr>
								<th> Item Name </th>
								<td> <%= item.getName() %> </td>
							</tr>
							<tr>
								<th> Description </th>
								<td> <%= item.getDescription() %> </td>
							</tr>
							<tr>
								<th> Payment Amount </th>
								<td> <%= item.getBuyPrice() %> </td>
							</tr>
							<tr> 
								<th> Credit Card Number </th>
								<td> <input type="text" name="card-number"> </td>
							</tr>
						</table>
						<input type="submit" class="btn btn-primary" value="Pay Now">
					</form>
				<% } %>
			</div>
		</div>
	</body>	
</html>