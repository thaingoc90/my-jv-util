var Comment = {
	initEvent : function() {
		$(document).on('keydown', '#frm-new-post textarea', function(e) {
			if (e.keyCode == 13 && !e.shiftKey) {
				e.preventDefault();
				Comment.PostComment();
			}
		});

		$(document).on('submit', '#frm-new-post', function() {
			Comment.PostComment();
		});
	},

	/** Post a comment */
	PostComment : function() {
		var targetId = $('section#comment').attr('data-target-id');
		var token = $('section#comment').attr('data-token');
		var content = $('#frm-new-post textarea').val();
		$.ajax({
			url : '/comment/post',
			type : 'POST',
			data : {
				"targetId" : targetId,
				"token" : token,
				"content" : content,
			},
			dataType : 'json',
			success : function(data) {
				console.log(data.message);
			},
			error : function(content) {
				alert("error");
			}
		});
	},
};
