function isEmpty(str) {
	return (!str || 0 === str.length);
}

function isBlank(str) {
	return (!str || /^\s*$/.test(str));
}

function clearInput(formId) {
	$('#' + formId + ' input').val('');
}

function toLink(link) {
	document.location.href = link;
}

$(document).on('click', '.modal-delete-open', function() {
	var link = $(this).attr("data-href");
	$('#deleteModal .modal-footer a').attr("href", link);
});

/* USERS */
$(document).on('click', '.modal-edit-user', function() {
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
$(document).on('click', '.modal-edit-group', function() {
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

$(document)
		.on(
				'change',
				"#permMappingModel input[type='checkbox']",
				function() {
					$(this).parent().siblings('.pad-lef-20').find(
							"input[type='checkbox']").prop('checked',
							this.checked);
					if ($(this).is(":checked")) {
						$(this).parents('.pad-lef-20').siblings().find(
								"input[type='checkbox']").prop('checked',
								this.checked);
					}
				});

$(document).on('click', '.modal-permission-mapping', function() {
	var groupId = $(this).attr("data-id");
	var groupName = $(this).attr("data-group-name");

	$('#permMappingModel #inputId').attr('value', groupId);
	$('#permMappingModel #inputName').text(groupName);
	$("#permMappingModel input[type='checkbox']").prop('checked', false);
	var permissions = gpMapping[groupId];
	for ( var index in permissions) {
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

/* TOKEN */
$(document).on('click', '.modal-edit-token', function() {
	var grandFather = $(this).parent().parent();
	var token = grandFather.find(".token").html();
	var cmtType = grandFather.find(".comment_type").html();
	var targetDomainsObj = grandFather.find(".target_domains").html();
	var targetDomains = $.parseJSON(targetDomainsObj);

	$('#editModal #inputToken').attr('value', token);
	$('#editModal #inputToken').siblings('span').html(token);
	$('#editModal #inputCommentType').val('0');
	$('#editModal #inputCommentType').val(cmtType);

	$('#editModal .target-domains input').attr('value', targetDomains[0]);
	$('#editModal .delete-domain').click();
	for ( var i = 1; i < targetDomains.length; i++) {
		$('#editModal .add-domain').click();
		var inputObj = $('#editModal .target-domains input')[i];
		$(inputObj).attr('value', targetDomains[i]);
	}

});

$(document).on('click', '.modal-edit-comment', function() {
	var token = $('.body-content').attr('data-token');
	var grandFather = $(this).parent().parent();
	var commentId = grandFather.find(".comment_id").html();
	var targetId = grandFather.find(".target_id").html();
	var user = grandFather.find(".account-name").html();
	var status = grandFather.find(".status").html();
	var content = grandFather.find(".content").html();

	$('#editModal #inputToken').attr('value', token);
	$('#editModal #inputCommentId').attr('value', commentId);
	$('#editModal #inputTargetId').attr('value', targetId);
	$('#editModal #inputUser').html(user);

	$('#editModal #inputStatus').val('0');
	$('#editModal #inputStatus').val(status);
	$('#editModal #inputContent').val(content);
});

$(document).on('click', '.modal-detail-comment', function() {
	var token = $('.body-content').attr('data-token');
	var commentId = $(this).attr('data-comment-id');
	var targetId = $(this).attr('data-target-id');

	$.ajax({
		url : '/admin/comment/list/detail',
		type : 'POST',
		data : {
			"token" : token,
			"commentId" : commentId,
			"targetId" : targetId
		},
		dataType : 'json',
		success : function(data) {
			if (data.status == 200) {
				var mapResult = data.message;
				$('#detailModal #inputUser').html(mapResult.account_name);
				$('#detailModal #inputApprover').html(mapResult.approved_by);
				$('#detailModal #inputUpdater').html(mapResult.updated_by);
				console.log(mapResult.updated_by);
				if (mapResult.updated_by != null) {
					var updatedDate = new Date(mapResult.updated);
					$('#detailModal #inputUpdateTime').html(' (' + updatedDate.format("HH'h':MM '-' dd/mm/yyyy") + ' )');
				} else {
					$('#detailModal #inputUpdateTime').html('');
				}
				var createdDate = new Date(mapResult.created);
				$('#detailModal #inputCreateTime').html(createdDate.format("HH'h':MM '-' dd/mm/yyyy"));
				$('#detailModal #inputStatus').html(mapResult.status_str);
				$('#detailModal #inputContent').html( mapResult.content.replace(/\n/g, '<br />'));
			}
		},
		error : function(content) {
			console.log("error");
		}
	});

});
