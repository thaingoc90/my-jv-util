$( '.modal-delete-open').bind('click', function() {
	var link = $(this).attr("data-href");
	$('#deleteModal .modal-footer a').attr("href", link);
});

$( '.modal-edit-user').bind('click', function() {
	var id = $(this).attr("data-id");
	var grandFather = $(this).parent().parent();
	var loginName = grandFather.find(".user-login-name").html();
	var displayName = grandFather.find(".user-display-name").html();
	var email = grandFather.find(".user-email").html();
	var groupId = grandFather.find(".user-group").html();

	$('#editModal #inputId').attr('value', id);
	$('#editModal #inputLoginName').html(loginName);
	$('#editModal #inputDisplayName').attr('value', displayName);
	$('#editModal #inputEmail').html(email);
	
	$("#editModal #inputGroup").val(groupId);
});