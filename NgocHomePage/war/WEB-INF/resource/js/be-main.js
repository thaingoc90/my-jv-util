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

/* USERS */
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

/* GROUPS */
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

$(document).on('change', "#permMappingModel input[type='checkbox']", function() {
	$(this).parent().siblings('.pad-lef-20').find("input[type='checkbox']").prop('checked', this.checked);
	if ($(this).is(":checked")) {
		$(this).parents('.pad-lef-20').siblings().find("input[type='checkbox']").prop('checked', this.checked);
	}
});

$(document).on('click', '.modal-permission-mapping', function() {
	var groupId = $(this).attr("data-id");
	var groupName = $(this).attr("data-group-name");
	
	$('#permMappingModel #inputId').attr('value', groupId);
	$('#permMappingModel #inputName').text(groupName);
	$("#permMappingModel input[type='checkbox']").prop('checked', false);
	var permissions = gpMapping[groupId];
	var listPermId = new Array();
	for (var index in permissions) {
		perm = permissions[index];
		permId = perm.id;
		$('#permMappingModel #' + permId).prop('checked', true);
	}
});

/* PERMISSIONS */
$(document).on('click', '.modal-edit-permission', function() {
	var grandFather = $(this).parent().parent();
	var id = grandFather.find(".perm-id").html();
	var desc = grandFather.find(".perm-desc").html();
	var parentId = grandFather.find(".perm-parent-id").html();
	
	$('#editModal #inputId').attr('value', id);
	$('#editModal #labelId').html(id);
	$('#editModal #inputDesc').attr('value', desc);
	$("#editModal #inputPid").val('0');
	if (!isEmpty(parentId)) {
		$("#editModal #inputPid").val(parentId);
	}
});

/* MENUS */
$(document).on('click', '.modal-edit-menu', function() {
	var grandFather = $(this).parent().parent();
	var id = $(this).attr("data-id");
	var name = grandFather.find(".menu-name").html();
	var url = grandFather.find(".menu-url").html();
	var position = grandFather.find(".menu-position").html();
	var parentId = grandFather.find(".menu-parent").html();
	var permission = grandFather.find(".menu-permission").html();
	
	$('#editModal #inputId').attr('value', id);
	$('#editModal #inputName').attr('value', name);
	$('#editModal #inputUrl').attr('value', url);
	$('#editModal #inputPosition').attr('value', position);
	$("#editModal #inputParentId").val('0');
	$("#editModal #inputPermission").val('0');
	if (!isEmpty(parentId)) {
		$("#editModal #inputParentId").val(parentId);
	}
	if (!isEmpty(permission)) {
		$("#editModal #inputPermission").val(permission);
	}
});