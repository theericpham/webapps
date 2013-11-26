<%@ page import="edu.ucla.cs.cs144.*" %>
<% Item item = (Item) request.getAttribute("item"); %>
<% User seller = (User) request.getAttribute("seller"); %>
<% String categories = (String) request.getAttribute("categories"); %>
<% Bid[] bids = (Bid[]) request.getAttribute("bids"); %>

<html>

<head>
	<title> Item Servlet Response </title> 
	<link rel="stylesheet" type="text/css" href="css/main.css">
	<link rel="stylesheet" type="text/css" href="css/bootstrap.css">
	<meta name="viewport" content="initial-scale=1.0, user-scalable=no" /> 
	<% if (request.getAttribute("found").equals("yes")) { %>
		<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"> </script> 
		<script type="text/javascript"> 
	  	function initialize() { 
		    var latlng = new google.maps.LatLng(34.063509,-118.44541); 
		    var myOptions = { 
		      zoom: 14, // default is 8  
		      center: latlng, 
		      mapTypeId: google.maps.MapTypeId.ROADMAP 
		    }; 
		    var map = new google.maps.Map(document.getElementById("map"), myOptions);

		    var geocoder = new google.maps.Geocoder();
		    var fullAddress = "<%= seller.getLocationCountry() %>";
		    var city = "<%= seller.getLocation() %>";
		    var country = "<%= seller.getCountry() %>";
		    var usa = "USA";

		    // geocode by fullAddress, then city, then country, then general coordinates
		    geocoder.geocode({"address": fullAddress}, function(results, status) {
	    		if (status == google.maps.GeocoderStatus.OK) {
	    			map.setCenter(results[0].geometry.location);
	    			map.fitBounds(results[0].geometry.viewport);
	    			var marker = new google.maps.Marker({map: map, position: results[0].geometry.location});
	    		}
	    		else {
	    			geocoder.geocode({ "address": city}, function(results, status) {
	    				if (status == google.maps.GeocoderStatus.OK) {
			    			map.setCenter(results[0].geometry.location);
			    			map.fitBounds(results[0].geometry.viewport);
			    			var marker = new google.maps.Marker({map: map, position: results[0].geometry.location});
			    		}
			    		else {
			    			geocoder.geocode({ "address": country}, function(results, status) {
			    				if (status == google.maps.GeocoderStatus.OK) {
					    			map.setCenter(results[0].geometry.location);
					    			map.fitBounds(results[0].geometry.viewport);
					    			var marker = new google.maps.Marker({map: map, position: results[0].geometry.location});
					    		}
					    		else {
					    			geocoder.geocode({"address": usa}, function(results, status) {
					    				map.setCenter(results[0].geometry.location);
						    			map.fitBounds(results[0].geometry.viewport);
						    			var marker = new google.maps.Marker({map: map, position: results[0].geometry.location});
					    			})
					    		}
			    			})
			    		}
	    			})
	    		}
		    });
	  	} 
		</script>
	<% } else { %>
		<script type="text/javascript">
		function initialize() {}
		</script>
	<% } %>
	
</head>

<body onload="initialize()">
	<div class="navbar navbar-default navbar-fixed-top search" role="navigation">
		<form action="item" method="GET" class="navbar-form navbar-left" role="search"> 
			<div class="form-group"> <label class="control-label"> Find an Item: </label> </div>
			<div class="form-group"> <input type="text" name="id" class="form-control" placeholder="ItemID"> </div>			
			<button type="submit" class="btn btn-primary"> Go! </button>
		</form>
	</div>
	<div class="container">
		<% if (request.getAttribute("found").equals("yes")) { %>

			<div class="row">
				<div class="col-xs-12">
					<div class="jumbotron">
						<h1 class="text-center"> Item <%= request.getAttribute("itemId") %> Found! </h1>
					</div>
				</div>
			</div> <!-- row -->

			<div class="row">
				<div class="col-sm-12 item-container">
					<div class="col-xs-12">
						<div class="alert alert-success">
							<h1 class="text-center"> Basic Item Information </h1>
						</div>
					</div>
					<table class="table table-bordered table-hover table-striped">
						<tr>
							<th> Name </th>
							<td> <%= item.getName() %> </td>
						</tr>
						<tr>
							<th> Current Highest Bid </th>
							<td> <%= item.getCurrentPrice() %> </td>
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
						<tr>
							<th> Description </th>
							<td> <%= item.getDescription() %> </td>
						</tr>
					</table>


				</div> 
			</div> <!-- row -->

			<div class="row">
				<div class="col-sm-12 map-container">
					<div class="col-xs-12">
						<div class="alert alert-info">
							<h1 class="text-center"> Seller Information </h1>
						</div>
					</div>
					<div class="col-sm-4">
						<table class="table table-bordered table-hover table-striped">
							<tr>
								<th> Seller Name </th>
								<td> <%= seller.getId() %> </td>
							</tr>
							<tr>
								<th> Seller Rating </th>
								<td> <%= seller.getRating() %> </td>
							</tr>
							<tr>
								<th> Seller Location </th>
								<td> <%= seller.getLocationCountry() %> </td>
							</tr>
						</table>
					</div> <!-- col-sm-4 -->
					<div class="col-sm-8">
						<div id="map"> </div>
					</div> <!-- col-sm-8 -->
				</div> <!-- map-container -->
			</div> <!-- row -->

			<div class="row">
				<div class="col-sm-12 bid-container">
					<% if (bids.length == 1) { %>
						<div class="col-xs-12">
							<div class="alert alert-warning">
								<h1 class="text-center"> This item has 1 bid </h1>
							</div>
						</div>
					<% } else { %>
						<div class="col-xs-12">
							<div class="alert alert-warning">
								<h1 class="text-center"> This item has <%= bids.length %> bids </h1>
							</div>
						</div>
					<% } %>

					<% if (bids.length != 0) { %>
						<table class="table table-bordered table-hover table-striped">
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
								<% for (int i = bids.length-1; i > -1; i--) { %>
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
				</div> <!-- bid-container -- >
			</div> <!-- row -- >

		<% } else { %>

			<div class="jumbotron">
				<h1> Item <%= request.getAttribute("itemId") %> Not Found </h1>
				<div class="alert alert-danger"> We were unable to find an item with the requested ID. </div>
			</div>

		<% } %>
	</div> <!-- container -->
</body>

</html>