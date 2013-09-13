var Comment = {
	
	/**
	 * Declares constants.
	 */
	constants : {
		refreshTime : 300000, // 5m
		taMaxWord : 500,
		pageRange : 4,
		timeagoPeriod: 30000, // 30s
	},
		
	/**
	 * Init method.
	 */
	initEvent : function() {
		Comment.GetComments();

		/* Refresh comments*/
		if (Comment.constants.refreshTime > 0) {
			setInterval(function() {
				Comment.GetComments();
			}, Comment.constants.refreshTime);
		}
		
		/* Update timeago */
		setInterval(function() {
			$(".timeago").timeago();
		}, Comment.constants.timeagoPeriod);

		/* Set enter-key is submit-button*/
		$(document).on('keydown', '.cmt-new-post textarea', function(e) {
			if (e.keyCode == 13 && !e.shiftKey) {
				e.preventDefault();
				Comment.PostComment();
			}
			Comment.setHeightIframe();
		});

		/*  Counts remain character*/
		$(document).on('keyup', '.cmt-new-post textarea', function(e) {
			$(".new-post-note >span").text(Comment.constants.taMaxWord - $(this).val().length);
		});

		/* Hide/Show remain character*/
		$(document).on('focus', '.cmt-new-post textarea', function(e) {
			$(".new-post-note").show();
		});

		$(document).on('blur', '.cmt-new-post textarea', function(e) {
			$(".new-post-note").hide();
		});
		
		$(document).on('focus', '.reply-post-message textarea', function(e) {
			$(this).parents('.reply-post-body').find('.reply-post-note').show();
		});

		$(document).on('blur', '.reply-post-message textarea', function(e) {
			$(this).parents('.reply-post-body').find('.reply-post-note').hide();
		});

		/* Click reply*/
		$(document).on('click', '.cmt-reply', function() {
			$(this).parents('.cmt-body').siblings('.cmt-reply-post').toggle();
			Comment.GetChildComments(this);
			Comment.setHeightIframe();
		});
		
		/* Reply comment*/
		$(document).on('keydown', '.cmt-reply-post textarea', function(e) {
			if (e.keyCode == 13 && !e.shiftKey) {
				e.preventDefault();
				Comment.ReplyComment(this);
			}
			Comment.setHeightIframe();
		});
		
		/* Processing paging */
		$(document).on('click', '.paging a', function() {
			var page = $(this).attr('data-p');
			Comment.Paging(page);
		});
		
		/* Like */
		$(document).on('click', '.cmt-like', function() {
			var cmtEle = $(this).closest('.cmt-item');
			var liked = $(this).attr('data-like');
			Comment.LikeComment(cmtEle, parseInt(liked));
			Comment.RenderLike(cmtEle);
		});
		
		/* Dislike */
		$(document).on('click', '.cmt-dislike', function() {
			var cmtEle = $(this).closest('.cmt-item');
			var disliked = $(this).attr('data-dislike');
			Comment.DislikeComment(cmtEle, parseInt(disliked));
			Comment.RenderLike(cmtEle);
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
		maxPage = maxPage < 0 ? 0 : maxPage;
		$('.paging').html('');
		var result = "";
		var startPage = 1, endPage = maxPage;
		var range = Comment.constants.pageRange;
		var temp = parseInt(range / 2);

		if (maxPage <= 1) {
			return;
		}

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

		result += "<a href='javascript:;' data-p='first'>&laquo;</a>";
		result += "<a href='javascript:;' data-p='pre'>&lt;</a>";
		for ( var i = startPage; i <= endPage; i++) {
			var rs = "<a href='javascript:;' data-p='" + i + "' class='"
					+ (i == page ? "active" : "") + "'>" + i + "</a>";
			result += rs;
		}
		result += "<a href='javascript:;' data-p='next'>&gt;</a>";
		result += "<a href='javascript:;' data-p='last'>&raquo;</a>";
		$('.paging').html(result);
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
					console.log(data.status);
				}
			},
			error : function(content) {
				console.log("error");
			}
		});
	},

	/**
	 * Renders list comments.
	 */
	RenderComment : function(comment) {
		var result = this.CountLikesOfComment(comment.comment_id, comment.target_id);
		var totalLikes = result.totalLikes;
		var flagUser = result.flagUser;
		
		var resultDl = this.CountDislikesOfComment(comment.comment_id, comment.target_id);
		var totalDislikes = resultDl.totalDislikes;
		var flagUserDl = resultDl.flagUser;
		
		var countChild = this.CountChildComment(comment.comment_id);
		
		var createdDate = new Date(comment.created);
		var result = "";
		result += "<article class='cmt-item clearfix' data-user-name='" + comment.account_name + "'data-comment-id='" + comment.comment_id + "'>";
		result += 	"<div class='cmt-avatar'>";
		result += 		"<img src='" + staticResourceRoot + "/images/icon/avatar-default.jpg'/>";
		result += 	"</div>";
		result += 	"<div class='cmt-body'>";
		result += 		"<span class='cmt-user-name'>";
		result += 			"<a href='javascript:;'>" + comment.account_name + "</a> ";
		result += 		"</span>";
		result += 		"<span class='cmt-content'>" + comment.content.replace(/\n/g, '<br />') + "</span>";
		result += 		"<div class='cmt-toolbox'>";
		result += 			"<span>";
		result += 				"<a href='javascript:;' class='cmt-like' data-like='" + flagUser + "'>";
		result += 					(flagUser == 1 ? "Unlike" : "Like");
		result += 				"</a>";
		result +=				"<span class='icon-like' style='display:" + (totalLikes > 0 ? "inline'>" : "none'>");
		result += 					"<img src='" + staticResourceRoot + "/images/icon/like-icon.png' />";
		result += 					"<span class='total-likes'>" + totalLikes + "</span>";
		result += 				"</span>";
		result += 			"</span>";
		result += 			"<span>";
		result += 				"<a href='javascript:;' class='cmt-dislike' data-dislike='" + flagUserDl + "'>";
		result += 					(flagUserDl == 1 ? "UnDislike" : "Dislike");
		result += 				"</a>";
		result +=				"<span class='icon-dislike' style='display:" + (totalDislikes > 0 ? "inline'>" : "none'>");
		result += 					"<img src='" + staticResourceRoot + "/images/icon/dislike-icon.png' /> ";
		result += 					"<span class='total-dislikes'>" + totalDislikes + "</span>";
		result += 				"</span>";
		result += 			"</span>";
		result += 			"<span>";
		result +=				"<a href='javascript:;' class='cmt-reply'>";
		result +=					"Reply <span>" + (countChild > 0 ? ("(" + countChild + ")") : "") + "</span>"; 
		result +=				"</a>";
		result +=			"</span>";
		result += 			"<span class='cmt-time timeago' title='" + createdDate.toISOString() + "'>" 
								+ createdDate.format("dd-mm-yyyy 'at' HH:MM") + "</span>";
		result += 		"</div>";
		result += 	"</div>";
		result += 	"<div class='clear'></div>";
		result += 	"<div class='cmt-reply-post'>";
		result += 		"<div class='reply-post-list'>";
		result +=		"</div>";
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
					$('section#comment').attr('data-page', 1);
					Comment.GetComments();
					Comment.showSuccessPostCmt(data.message);
				} else {
					Comment.showErrorPostCmt(data.message);
				}
			},
			error : function(content) {
				console.log("error");
			}
		});
	},
	
	/** Counts the number of comment's child. */
	CountChildComment : function(parentCommentId) {
		var targetId = $('section#comment').attr('data-target-id');
		var token = $('section#comment').attr('data-token');
		var result = 0;
		$.ajax({
			url : '/comment/countChildComment',
			type : 'POST',
			data : {
				"targetId" : targetId,
				"token" : token,
				"parentCommentId" : parentCommentId,
			},
			async: false,
			dataType : 'json',
			success : function(data) {
				if (data.status == 200) {
					result = parseInt(data.message);
				} 
			},
			error : function(content) {
				console.log("error");
			}
		});
		return result;
	},
	
	/* Gets list of child comments*/
	GetChildComments : function(ele) {
		var targetId = $('section#comment').attr('data-target-id');
		var token = $('section#comment').attr('data-token');
		var parentCommentId = $(ele).parents('article').attr('data-comment-id');
		$.ajax({
			url : '/comment/getChildComments',
			type : 'POST',
			data : {
				"targetId" : targetId,
				"token" : token,
				"parentCommentId" : parentCommentId,
			},
			dataType : 'json',
			success : function(data) {
				if (data.status == 200) {
					var numChildCmts = data.message.numChildComments;
					var htmlCount = numChildCmts > 0 ? ("(" + numChildCmts + ")") : "";
					$(ele).find('span').html(htmlCount);
					$(ele).parents('article').find('.reply-post-list').html('');
					var listChildCmts = data.message.listChildComments;
					if (listChildCmts == null || listChildCmts.length == 0) {
						return;
					}
					for ( var i = listChildCmts.length - 1; i >= 0; i--) {
						var result = Comment.RenderChildComment(listChildCmts[i]);
						$(ele).parents('article').find('.reply-post-list').append(result);
					}
					$(".timeago").timeago();
					Comment.setHeightIframe();
				} 
			},
			error : function(content) {
				console.log("error");
			}
		});
	}, 
	
	/** Render comment's child */
	RenderChildComment : function(comment) {
		var result = this.CountLikesOfComment(comment.comment_id, comment.target_id);
		var totalLikes = result.totalLikes;
		var flagUser = result.flagUser;
		
		var resultDl = this.CountDislikesOfComment(comment.comment_id, comment.target_id);
		var totalDislikes = resultDl.totalDislikes;
		var flagUserDl = resultDl.flagUser;
		
		var createdDate = new Date(comment.created);
		var result = "";
		result += 	"<div class='cmt-item clearfix' data-user-name='" + comment.account_name + "'data-comment-id='" + comment.comment_id + "'>";
		result += 		"<div class='reply-post-avatar'>";
		result += 			"<img src='" + staticResourceRoot + "/images/icon/avatar-default.jpg'/>";
		result += 		"</div>";
		result += 		"<div class='reply-post-body'>";
		result += 			"<span class='cmt-user-name'>";
		result += 				"<a href='javascript:;'>" + comment.account_name + "</a> ";
		result += 			"</span>";	
		result += 			"<span class='cmt-content'>" + comment.content.replace(/\n/g, '<br />') + "</span>";
		result += 			"<div class='cmt-toolbox'>";
		result += 				"<span>";
		result += 					"<a href='javascript:;' class='cmt-like' data-like='" + flagUser + "'>";
		result += 						(flagUser == 1 ? "Unlike" : "Like");
		result += 					"</a>";
		result +=					"<span class='icon-like' style='display:" + (totalLikes > 0 ? "inline'>" : "none'>");
		result += 						"<img src='" + staticResourceRoot + "/images/icon/like-icon.png' />";
		result += 						"<span class='total-likes'>" + totalLikes + "</span>";
		result += 					"</span>";
		result += 				"</span>";
		result += 				"<span>";
		result += 					"<a href='javascript:;' class='cmt-dislike' data-dislike='" + flagUserDl + "'>";
		result += 						(flagUserDl == 1 ? "UnDislike" : "Dislike");
		result += 					"</a>";
		result +=					"<span class='icon-dislike' style='display:" + (totalDislikes > 0 ? "inline'>" : "none'>");
		result += 						"<img src='" + staticResourceRoot + "/images/icon/dislike-icon.png' /> ";
		result += 						"<span class='total-dislikes'>" + totalDislikes + "</span>";
		result +=					"</span>";
		result +=				"</span>";
		result +=				"<span class='cmt-time timeago' title='" + createdDate.toISOString() + "'>"; 
		result +=			"</div>";
		result +=		"</div>";
		result +=	"</div>";
		return result;
	},
	
	/** Reply a comment */
	ReplyComment : function(ele) {
		var targetId = $('section#comment').attr('data-target-id');
		var token = $('section#comment').attr('data-token');
		var parentCommentId = $(ele).parents('article').attr('data-comment-id');
		var content = $(ele).val();
		if (isBlank(content)) {
			return;
		}
		$.ajax({
			url : '/comment/post',
			type : 'POST',
			data : {
				"targetId" : targetId,
				"token" : token,
				"parentCommentId": parentCommentId,
				"content" : content,
			},
			dataType : 'json',
			success : function(data) {
				if (data.status == 200) {
					$(ele).val('');
					var replyEle = $(ele).parents('article').find('.cmt-reply');
					Comment.GetChildComments(replyEle);
					Comment.showSuccessPostCmt(data.message);
				} else {
					Comment.showErrorPostCmt(data.message);
				}
			},
			error : function(content) {
				console.log("error");
			}
		});
	},
	
	/*======================== LIKE ==================================*/	
	/**
	 * Update the total likes, icon-like, text(unlike or like).
	 * 
	 */
	RenderLike : function(cmtEle) {
		var commentId = $(cmtEle).attr('data-comment-id');
		var targetId = $('section#comment').attr('data-target-id');
		
		var result = this.CountLikesOfComment(commentId, targetId);
		var totalLikes = result.totalLikes;
		var flagUser = result.flagUser;
		$(cmtEle).find('.total-likes').text(totalLikes);
		$(cmtEle).find('.cmt-like').attr("data-like", flagUser);
		$(cmtEle).find('.cmt-like').text(flagUser == 1 ? "Unlike" : "Like");
		if (totalLikes > 0) {
			$(cmtEle).find('.icon-like').css("display", "inline");
		} else {
			$(cmtEle).find('.icon-like').css("display", "none");
		}
		
		var resultDl = this.CountDislikesOfComment(commentId, targetId);
		console.log(resultDl);
		var totalDislikes = resultDl.totalDislikes;
		var flagUserDl = resultDl.flagUser;
		$(cmtEle).find('.total-dislikes').text(totalDislikes);
		$(cmtEle).find('.cmt-dislike').attr("data-dislike", flagUserDl);
		$(cmtEle).find('.cmt-dislike').text(flagUserDl == 1 ? "UnDislike" : "Dislike");
		if (totalDislikes > 0) {
			$(cmtEle).find('.icon-dislike').css("display", "inline");
		} else {
			$(cmtEle).find('.icon-dislike').css("display", "none");
		}
	},
	
	/**
	 * Counts the number of like of a comment.
	 * 
	 */
	CountLikesOfComment : function(commentId, targetId) {
		var result = new Array();
		$.ajax({
			url : '/comment/countLikes',
			type : 'POST',
			async: false,
			data : {
				"targetId" : targetId,
				"commentId" : commentId,
			},
			dataType : 'json',
			success : function(data) {
				if (data.status == 200) {
					result["totalLikes"] = parseInt(data.message.totalLikes);
					result["flagUser"] = data.message.flagUser;
				}
			},
			error : function(content) {
				console.log("error");
			}
		});
		return result;
	},
	
	/**
	 * Like a comment.
	 * 
	 */
	LikeComment : function(cmtEle, isUnlike) {
		var commentId = $(cmtEle).attr('data-comment-id');
		var targetId = $('section#comment').attr('data-target-id');
		var result = 0;
		$.ajax({
			url : '/comment/' + (isUnlike == 1 ? 'unlike' : 'like'),
			type : 'POST',
			data : {
				"targetId" : targetId,
				"commentId" : commentId,
			},
			dataType : 'json',
			success : function(data) {
				if (data.status == 200) {
					result = parseInt(data.message);
				}
			},
			error : function(content) {
				console.log("error");
			}
		});
		return result;
	},
	
	/*======================== DISLIKE ==================================*/
	/**
	 * Counts the number of dislike of a comment.
	 * 
	 */
	CountDislikesOfComment : function(commentId, targetId) {
		var result = new Array();
		$.ajax({
			url : '/comment/countDislikes',
			type : 'POST',
			async: false,
			data : {
				"targetId" : targetId,
				"commentId" : commentId,
			},
			dataType : 'json',
			success : function(data) {
				if (data.status == 200) {
					result["totalDislikes"] = parseInt(data.message.totalDislikes);
					result["flagUser"] = data.message.flagUser;
				}
			},
			error : function(content) {
				console.log("error");
			}
		});
		return result;
	},
	
	/**
	 * Dislike a comment.
	 * 
	 */
	DislikeComment : function(cmtEle, isUnDislike) {
		var commentId = $(cmtEle).attr('data-comment-id');
		var targetId = $('section#comment').attr('data-target-id');
		var result = 0;
		$.ajax({
			url : '/comment/' + (isUnDislike == 1 ? 'unDislike' : 'dislike'),
			type : 'POST',
			data : {
				"targetId" : targetId,
				"commentId" : commentId,
			},
			dataType : 'json',
			success : function(data) {
				if (data.status == 200) {
					result = parseInt(data.message);
				}
			},
			error : function(content) {
				console.log("error");
			}
		});
		return result;
	},
	
	/** Reset Height Iframe * */
	setHeightIframe : function() {
		var height = $('body').height() + 25;
		var targetId = $('section#comment').attr('data-target-id');
		var token = $('section#comment').attr('data-token');
		// Parent have not used keyif yet.
		var keyif = token + "_" + targetId;
		var dataMessage = '{"action":"setHeight", "height":"' + height
				+ '", "target":"' + keyif + '"}';
		var referrerUrl = (window.location != window.parent.location) ? document.referrer
				: document.location.href;
		window.parent.postMessage(dataMessage, referrerUrl);
	},
};

$(document).ready(function() {
	Comment.initEvent();
});
