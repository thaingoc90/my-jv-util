function isEmpty(str) {
    return (!str || 0 === str.length);
}

function isBlank(str) {
    return (!str || /^\s*$/.test(str));
}

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

$( '.modal-edit-group').bind('click', function() {
	var id = $(this).attr("data-id");
	var grandFather = $(this).parent().parent();
	var name = grandFather.find(".group-name").html();
	var desc = grandFather.find(".group-desc").html();
	var system = grandFather.find(".group-system").html();

	$('#editModal #inputId').attr('value', id);
	$('#editModal #inputName').attr('value', name);
	$('#editModal #inputDesc').attr('value', desc);
	
	if (!isEmpty(system)) {
		$('#editModal #inputSystem').prop('checked', true);
	} else {
		$('#editModal #inputSystem').prop('checked', false);
	}
});

$(document).on('click', '.modal-permission-mapping', function() {
	var groupId = $(this).attr("data-id");
	var groupName = $(this).attr("data-group-name");
	
	$('#permMappingModel #inputId').attr('value', groupId);
	$('#permMappingModel #inputName').text(groupName);
	var permissions = gpMapping[groupId];
	var listPermId = new Array();
	for (var index in permissions) {
		perm = permissions[index];
		permId = perm.id;
		listPermId[index] = permId;
	}
	
	$('#permMappingModel #inputPermission').val(listPermId);
});