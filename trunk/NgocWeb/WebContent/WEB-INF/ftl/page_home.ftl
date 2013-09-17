<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8" />
    <meta name="description" content="description" />
    <meta name="keywords" content="contents" />
    <meta name="author" content="SPINNING" />
    <title>Home page</title>
    <style>
		.clock {
		    width: 200px;
		    margin: 0 auto;
		    padding: 10px 0px;
		    background: #FAEFB4;
		    border: 1px solid #ccc;
		    position: absolute;
			right: 5px;
			top: 5px;
		    border-radius: 10px;
		    -webkit-border-radius: 10px;
		    -moz-border-radius: 10px;
		}
		
		#Date {
		    font-family: 'BebasNeueRegular', Arial, Helvetica, sans-serif;
		    font-size: 14px;
		    text-align: center;
		    color: #BE3422;
		}
		
		ul {
		    margin: 0 auto;
		    padding: 0px;
		    list-style: none;
		    text-align: center;
		    color: #7BA771;
		}
		
		ul li {
		    display: inline;
		    margin-right: -3px;
		    font-size: 14px;
		    font-family: 'BebasNeueRegular', Arial, Helvetica, sans-serif;
		    font-weight: bold;
		}
		
		#point {
		    position: relative;
		    -moz-animation: mymove 1s ease infinite;
		    -webkit-animation: mymove 1s ease infinite;
		}
		
		/* Simple Animation */
		@-webkit-keyframes mymove {
		    0% {opacity: 1.0;
		    text-shadow: 0 0 20px #00c6ff;
		}
		
		50% {
		    opacity: 0;
		    text-shadow: none;
		}
		
		100% {
		    opacity: 1.0;
		    text-shadow: 0 0 20px #00c6ff;
		}	
		}
		
		@-moz-keyframes mymove {
		    0% {
		        opacity: 1.0;
		        text-shadow: 0 0 20px #00c6ff;
		    }
		
		    50% {
		        opacity: 0;
		        text-shadow: none;
		    }
		
		    100% {
		        opacity: 1.0;
		        text-shadow: 0 0 20px #00c6ff;
		    };
		}
    </style>
    
    <script type="text/javascript" src="${staticResourceRoot}js/jquery-2.0.2.min.js" charset="utf-8"></script>
</head>
<body>
	<h1>Spring MVC FreeMarker</h1>
 
	<h2>Hello World</h2> ${locale}
	
	<div class="commentNhp" data-target="123" data-token="ngoc" data-width="500px" data-height="auto" data-nums="3"></div>
	<script src="/static/js/comment-main.js"></script>
 
 	<div class="clock">
		<div id="Date"></div>
  		<ul>
	  		<li id="hours"></li>
	  		<li id="point">:</li>
	  		<li id="min"></li>
	  		<li id="point">:</li>
	  		<li id="sec"></li>
	  	</ul>
	</div>
	
	<script type="text/javascript">
		$(document).ready(function() {
			// Create two variable with the names of the months and days in an array
			var monthNames = [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ]; 
			var dayNames= ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]
			
			// Create a newDate() object
			var newDate = new Date();
			// Extract the current date from Date object
			newDate.setDate(newDate.getDate());
			// Output the day, date, month and year   
			$('#Date').html(dayNames[newDate.getDay()] + " " + newDate.getDate() + ' ' + monthNames[newDate.getMonth()] + ' ' + newDate.getFullYear());
			
			setInterval( function() {
				// Create a newDate() object and extract the seconds of the current time on the visitor's
				var seconds = new Date().getSeconds();
				// Add a leading zero to seconds value
				$("#sec").html(( seconds < 10 ? "0" : "" ) + seconds + "s");
				},1000);
				
			setInterval( function() {
				// Create a newDate() object and extract the minutes of the current time on the visitor's
				var minutes = new Date().getMinutes();
				// Add a leading zero to the minutes value
				$("#min").html(( minutes < 10 ? "0" : "" ) + minutes + "m");
			    },1000);
				
			setInterval( function() {
				// Create a newDate() object and extract the hours of the current time on the visitor's
				var hours = new Date().getHours();
				// Add a leading zero to the hours value
				$("#hours").html(( hours < 10 ? "0" : "" ) + hours + "h");
			    }, 1000);	
		});
	</script>
</body>
</html>