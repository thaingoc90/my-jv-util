<!DOCTYPE html>
<html>
<head>
	<title>Comment Box</title>

	<link rel="stylesheet" type="text/css" href="${staticResourceRoot}css/comment.css"/>

	<script type="text/javascript" src="${staticResourceRoot}js/jquery-2.0.2.min.js" charset="utf-8"></script>
	<script type="text/javascript" src="${staticResourceRoot}js/comment.js" charset="utf-8"></script>
</head>
<body>

	<section id="comment" data-target-id="123" data-token="ngoc">
		<header>
			Comments
		</header>
		<div class="cmt-header">
			<div class="totals">5 comments</div>
			<div class="paging">
				<a href="#">&laquo;</a>
				<a href="#">&lt;</a>
				<a href="#">1</a>
				<a href="#">2</a>
				<a href="#">3</a>
				...
				<a href="#">&gt;</a>
				<a href="#">&raquo;</a>
			</div>
		</div>
		<div class="cmt-list">
			<#list LIST_COMMENTS as comment>
				<article class="cmt-item clearfix" data-user-name="${comment.account_name}">  
					<div class="cmt-avatar">
						<img src="${staticResourceRoot}/images/icon/avatar-default.jpg"/>
					</div>
					<div class="cmt-body">
						<span class="cmt-user-name">
							<a href="#">${comment.account_name}</a> :
						</span>
						<span class="cmt-content">${comment.content}</span>
						<div class="cmt-toolbox">
							<span>
								<a href="#" class="cmt-like">Like</a> 
								<img src="${staticResourceRoot}/images/icon/like-icon.png" /> 
								<span class="totals-like">2</span>
							</span>
							<span>
								<a href="#" class="cmt-dislike">Dislike</a>
								<img src="${staticResourceRoot}/images/icon/dislike-icon.png" /> 
								<span class="totals-dislike">2</span>
							</span>	
							<span><a href="#" class="cmt-reply">Reply</a></span>
							<span class="cmt-time">${comment.created?datetime?string("EEEE dd-MM-yyyy 'at' hh:mm")}</span>
						</div>
					</div>
					<div class="clear"></div>
					<div class="cmt-reply-post">
						<div class="reply-post-avatar">
							<img src="${staticResourceRoot}/images/icon/avatar-default.jpg"/>
						</div>	
						<div class="reply-post-body">
							<div class="reply-post-message">
								<textarea placeholder="comment..." onkeyup="textAreaAdjust(this)" ></textarea>
							</div>
							<div>
								<span class="reply-post-note">Ký tự còn lại: 500</span>
								<span class="reply-post-error">Bạn chưa đăng nhập.</span>
							</div>
						</div>	
					</div>
				</article>
			</#list>
		</div>
		<div class="cmt-new-post">
			<form action="" method="post" id="frm-new-post">
				<div class="new-post-avatar">
					<img src="${staticResourceRoot}/images/icon/avatar-default.jpg"/>
				</div>	
				<div class="new-post-body">
					<div class="new-post-message">
						<textarea placeholder="comment..." onkeyup="textAreaAdjust(this)" ></textarea>
					</div>
					<div>
						<span class="new-post-note">Ký tự còn lại: 500</span>
						<span class="new-post-error">Bạn chưa đăng nhập.</span>
					</div>
				</div>	
			</form>
		</div>
	</section>

	<script>
	function textAreaAdjust(o) {
	    o.style.height = "1px";
	    o.style.height = (o.scrollHeight)+"px";
	}
	
	$(document).on('click', '.cmt-reply', function() {
		$(this).parents('.cmt-body').siblings('.cmt-reply-post').toggle();
	});
	
	Comment.initEvent();
	</script>
</body>
</html>