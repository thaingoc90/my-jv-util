$( '.modal-delete-open').bind('click', function() {
	var link = $(this).attr("data-href");
	$('#deleteModal .modal-footer a').attr("href", link);
});