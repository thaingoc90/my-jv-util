var Comment = {
	initEvent : function() {
		Comment.GetComments();
		
		setInterval(function() {
			Comment.GetComments();
		}, 60000);

		$(document).on('click', '.cmt-reply', function() {
			$(this).parents('.cmt-body').siblings('.cmt-reply-post').toggle();
			Comment.setHeightIframe();
		});
		
		$(document).on('keydown', '.cmt-new-post textarea', function(e) {
			if (e.keyCode == 13 && !e.shiftKey) {
				e.preventDefault();
				Comment.PostComment();
			}
		});
		
		$(document).on('keyup', '.cmt-new-post textarea', function(e) {
			$(".new-post-note >span").text(500 - $(this).val().length);
		});
		
		$(document).on('focus', '.cmt-new-post textarea', function(e) {
			$(".new-post-note").show();
		});
		
		$(document).on('blur', '.cmt-new-post textarea', function(e) {
			$(".new-post-note").hide();
		});
		
		
		/**
		 * Processing paging
		 */
		$(document).on('click', '.paging a', function() {
			var page = $(this).attr('data-p');
			Comment.Paging(page);
		});
	},

	showErrorPostCmt : function(msg) {
		$('.new-post-error').html(msg);
		$('.new-post-error').show();
		setTimeout(function() {
			$('.new-post-error').fadeOut("slow");
			$('.new-post-error').html('');
		}, 4000);
	},

	showSuccessPostCmt : function(msg) {
		$('.new-post-success').html(msg);
		$('.new-post-success').show();
		setTimeout(function() {
			$('.new-post-success').fadeOut("slow");
			$('.new-post-success').html('');
		}, 4000);
	},

	/**
	 * Processes the paging
	 */
	Paging : function(page) {
		var curPage = $('section#comment').attr('data-page');
		var maxPage = $('section#comment').attr('data-max-page');
		curPage = parseInt(curPage);
		if (page == "first") {
			curPage = 1;
		} else if (page == "pre") {
			curPage = curPage > 1 ? curPage - 1 : 1;
		} else if (page == "next") {
			curPage = curPage < maxPage ? curPage + 1 : curPage;
		} else if (page == "last") {
			curPage = maxPage;
		} else {
			curPage = page;
		}
		$('section#comment').attr('data-page', curPage);
		Comment.RenderPaging();
		Comment.GetComments();
	},
	
	/**
	 * Renders the current paging's state
	 */
	RenderPaging : function() {
		var page = $('section#comment').attr('data-page');
		page = parseInt(page);
		page = page < 1 ? 1 : page;
		var maxPage = $('section#comment').attr('data-max-page');
		maxPage = parseInt(maxPage);
		maxPage = maxPage < 1 ? 1 : maxPage;
		$('.paging>span').html('');
		var result = "";
		var startPage = 1, endPage = maxPage;
		var range = 4;
		var temp = parseInt(range / 2);

		if (range < maxPage) {
			if (page - temp < 1) {
				endPage = startPage + range;
			} else if (page + range - temp > maxPage) {
				startPage = endPage - range;
			} else {
				startPage = page - temp;
				endPage = startPage + range;
			}
		}
		for ( var i = startPage; i <= endPage; i++) {
			var rs = "<a href='javascript:;' data-p='" + i + "' class='"
					+ (i == page ? "active" : "") + "'>" + i + "</a>";
			result += rs;
		}
		$('.paging>span').html(result);
	},
	
	/**
	 * Gets list comments.
	 */
	GetComments : function() {
		var page = $('section#comment').attr('data-page');
		var limit = $('section#comment').attr('data-limit');
		limit = parseInt(limit, 10);
		var targetId = $('section#comment').attr('data-target-id');
		var token = $('section#comment').attr('data-token');
		$.ajax({
			url : '/comment/getComments',
			type : 'POST',
			data : {
				"targetId" : targetId,
				"token" : token,
				"limit" : limit,
				"page" : page
			},
			dataType : 'json',
			success : function(data) {
				if (data.status == 200) {
					var numCmts = data.message.numComments;
					$('.cmt-header .totals>span').html(numCmts);
					var maxPage = data.message.maxPage;
					$('section#comment').attr('data-max-page', maxPage);
					$('.cmt-list').html('');
					var listCmts = data.message.listComments;
					if (listCmts == null || listCmts.length == 0) {
						return;
					}
					for ( var i = listCmts.length - 1; i >= 0; i--) {
						var result = Comment.RenderComment(listCmts[i]);
						$('.cmt-list').append(result);
					}
					$(".timeago").timeago();
					Comment.RenderPaging();
					Comment.setHeightIframe();
				} else {
					alert(data.status);
				}
			},
			error : function(content) {
				alert("error");
			}
		});
	},

	/**
	 * Renders list comments. 
	 */
	RenderComment : function(comment) {
		var createdDate = new Date(comment.created);
		var result = "";
		result += "<article class='cmt-item clearfix' data-user-name='" + comment.account_name + "'>";
		result += 	"<div class='cmt-avatar'>";
		result += 		"<img src='" + staticResourceRoot + "/images/icon/avatar-default.jpg'/>";
		result += 	"</div>";
		result += 	"<div class='cmt-body'>";
		result += 		"<span class='cmt-user-name'>";
		result += 			"<a href='#'>" + comment.account_name + "</a> ";
		result += 		"</span>";
		result += 		"<span class='cmt-content'>" + comment.content.replace(/\n/g, '<br />'); + "</span>";
		result += 		"<div class='cmt-toolbox'>";
		result += 			"<span>";
		result += 				"<a href='#' class='cmt-like'>Like</a>";
		result += 				"<img src='" + staticResourceRoot + "/images/icon/like-icon.png' />";
		result += 				"<span class='totals-like'>2</span>";
		result += 			"</span>";
		result += 			"<span>";
		result += 				"<a href='#' class='cmt-dislike'>Dislike</a>";
		result += 				"<img src='" + staticResourceRoot + "/images/icon/dislike-icon.png' /> ";
		result += 				"<span class='totals-dislike'>2</span>";
		result += 			"</span>";
		result += 			"<span><a href='javascript:;' class='cmt-reply'>Reply</a></span>";
		result += 			"<span class='cmt-time timeago' title='" + createdDate.toISOString() + "'>" 
								+ createdDate.format("dd-mm-yyyy 'at' HH:MM") + "</span>";
		result += 		"</div>";
		result += 	"</div>";
		result += 	"<div class='clear'></div>";
		result += 	"<div class='cmt-reply-post'>";
		result += 		"<div class='reply-post-avatar'>";
		result += 			"<img src='" + staticResourceRoot + "/images/icon/avatar-default.jpg'/>";
		result += 		"</div>";
		result += 		"<div class='reply-post-body'>";
		result += 			"<div class='reply-post-message'>";
		result += 				"<textarea placeholder='Write a comment ...' onkeyup='textAreaAdjust(this)' ></textarea>";
		result += 			"</div>";
		result += 			"<div>";
		result += 				"<span class='reply-post-note'>Remaining characters: <span>500</span></span>";
		result += 				"<span class='reply-post-error'>Bạn chưa đăng nhập.</span>";
		result += 				"<span class='reply-post-success'></span>";
		result += 			"</div>";
		result += 		"</div>";
		result += 	"</div>";
		result += "</article>";
		return result;
	},

	/** Post a comment */
	PostComment : function() {
		var targetId = $('section#comment').attr('data-target-id');
		var token = $('section#comment').attr('data-token');
		var content = $('#frm-new-post textarea').val();
		if (isBlank(content)) {
			return;
		}
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
				if (data.status == 200) {
					$('#frm-new-post textarea').val('');
					Comment.GetComments(1);
					Comment.showSuccessPostCmt(data.message);
				} else {
					Comment.showErrorPostCmt(data.message);
				}
			},
			error : function(content) {
				alert("error");
			}
		});
	},
	
	/** Reset Height Iframe **/
    setHeightIframe: function() {
        var height = $('body').height() + 25;
        var targetId = $('section#comment').attr('data-target-id');
		var token = $('section#comment').attr('data-token');
        var keyif = token + "_" + targetId;
        var dataMessage = '{"action":"setHeight", "height":"'+height+'", "target":"'+keyif+'"}';
        var referrerUrl = (window.location != window.parent.location) ? document.referrer : document.location.href;
        window.parent.postMessage(dataMessage, referrerUrl);
    },
};

$(document).ready(function() {
	Comment.initEvent();
});
