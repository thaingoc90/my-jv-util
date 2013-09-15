$(document).on('click', '.add-domain', function() {
	var listDomains = $(this).siblings('.list-domains');
	var domain = "<div class='target-domains' >" +
					"<input type='text' name='targetDomains' required placeholder='Domain'> " +
					"<a href='javascript:;' class='delete-domain'>Remove</a>" +
				 "</div>";
	$(listDomains).append(domain);
});

$(document).on('click', '.delete-domain', function() {
	$(this).parent().remove();
});