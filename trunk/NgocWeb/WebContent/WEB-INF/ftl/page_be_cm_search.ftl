<#import "inc_layout.ftl" as layout />
<@layout.masterTemplate title="Comment Search Token">

	<div class="search-token ta-cen" style="margin: 12% auto; width: 400px;">
		<#include "inc_error_zone.ftl" />
		<form class="form-search" onsubmit="return text();">
		  	<input type="text" class="input-medium search-query span3" placeholder="Token">
		  	<button type="submit" class="btn">List</button>
		</form>
	</div>

	<script>
		var text = function() {
			var curUrl = window.location.href;
			var token = $('.search-token input').val();
			if (!isBlank(token)) {
				window.location.href = curUrl + '/' + token;
			}
			return false;
		}
	</script>
</@layout.masterTemplate>