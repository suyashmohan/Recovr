
(function($){
	var loginURL = "http://192.168.1.117:3000/login";
	var coordinateURL = "http://192.168.1.117:3000/coordinate"
	var phone = "";
	var app = $.sammy('#main', function(){
		this.use('Handlebars', 'html');
		this.get('#/', function(){
			var that = this;
			this.partial('pages/home.html', function(){
				$("#loginBtn").click(function(){
					var _phone = $("#phone").val();
					var _password = $("#password").val();
					$.ajax({
						type : "POST",
						url : loginURL,
						data : {"phone" : _phone, "password":_password}
					}).done(function(result){
						$("#msgLog").html("Success");
						var json = JSON.parse(result);
						if(json.response == "true")
						{
							phone = _phone;
							that.redirect("#/profile");
						}
					}).fail(function( jqXHR, textStatus){
						$("#msgLog").html("Login failed. Please re-check your details");
					});
				});
			});
		});

		this.get('#/profile', function(){
			this.phone = phone;			
			var that = this;	
			this.partial('pages/profile.html', function(){
				if(phone != "")
				{
					$("#lastBtn").click(function(){
						that.redirect("#/map");
					});

					$("#lastPathBtn").click(function(){
						that.redirect("#/map_path");
					});
				}
				else
				{
					$("#parent").html("Invalid Usage");
				}
			});
		});

		this.get('#/map', function(){
			this.phone = phone;
			var that = this;
			this.partial('pages/map.html', function(){	
				$("#map-canvas").html("Fetching track...");

				$("#lastBtn").click(function(){
					that.redirect("#/map");
				});

				$("#lastPathBtn").click(function(){
					that.redirect("#/map_path");
				});

				if(phone != "")
				{
					$.ajax({
						type : "GET",
						url : coordinateURL+"?range=1&phone="+phone
					}).done(function(result){
						var json = JSON.parse(result);

						var lat = json[0].Lng;
						var lng = json[0].Lat;

						latLng = new google.maps.LatLng(lat,lng);
						var mapOptions = {
						  zoom: 15,
						  center: latLng
						}
						var map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);
						
						var marker = new google.maps.Marker({
						    position: latLng,
						    title:"Your phone was here last time"
						});
						marker.setMap(map);	

					}).fail(function( jqXHR, textStatus){
						$("#map-canvas").html("Error retrieving coordinates");
					});
				}		
				else
				{
					$("#parent").html("Invalid Usage");
				}	
			});
		})

		this.get("#/map_path", function(){
			this.phone = phone;
			var that = this;
			this.partial('pages/map.html', function(){	
				$("#map-canvas").html("Fetching track...");

				$("#lastBtn").click(function(){
					that.redirect("#/map");
				});

				$("#lastPathBtn").click(function(){
					that.redirect("#/map_path");
				});

				if(phone != "")
				{
					$.ajax({
						type : "GET",
						url : coordinateURL+"?range=100&phone="+phone
					}).done(function(result){
						var json = JSON.parse(result);

						var lat = parseFloat(json[0].Lng);
						var lng = parseFloat(json[0].Lat);

						var latLng = new google.maps.LatLng(lat,lng);
						var mapOptions = {
						  zoom: 15,
						  center: latLng
						}
						var map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);
						
						var pathLatLng = [];
						for(point in json)
						{							
							pathLatLng.push(new google.maps.LatLng(parseFloat(json[point].Lng), parseFloat(json[point].Lat)));
						}	

						var Path = new google.maps.Polyline({
						    path: pathLatLng,
						    geodesic: true,
						    strokeColor: '#FF0000',
						    strokeOpacity: 1.0,
						    strokeWeight: 2
						});

						Path.setMap(map);

						var marker = new google.maps.Marker({
						    position: latLng,
						    title:"Your phone was here last time"
						});
						marker.setMap(map);	


					}).fail(function( jqXHR, textStatus){
						$("#map-canvas").html("Error retrieving coordinates");
					});
				}		
				else
				{
					$("#parent").html("Invalid Usage");
				}	
			});
		});
	});

	$(function(){
		app.run('#/');
	})
})(jQuery);