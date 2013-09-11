<!DOCTYPE html>
<html>
<head>
	<title>Comment Box</title>

	<link rel="stylesheet" type="text/css" href="${staticResourceRoot}css/comment.css"/>

	<script type="text/javascript" src="${staticResourceRoot}js/jquery-2.0.2.min.js" charset="utf-8"></script>
	<script type="text/javascript" src="${staticResourceRoot}js/comment-base.js" charset="utf-8"></script>
	<script type="text/javascript" src="${staticResourceRoot}js/comment.js" charset="utf-8"></script>
	<script type="text/javascript" src="${staticResourceRoot}js/jquery.timeago.js" charset="utf-8"></script>
	<script type="text/javascript" src="${staticResourceRoot}js/date.format.js" charset="utf-8"></script>
	<script>
		var staticResourceRoot = "${staticResourceRoot}";
	</script>
</head>
<body>

	<section id="comment" data-target-id="123" data-token="ngoc" data-limit="${limit}" data-page="${curPage}" data-max-page="${maxPage}">
		<div class="cmt-header">
			<div class="totals"><span>${numComments}</span> comment(s)</div>
			<div class="paging">
				<a href="javascript:;" data-p='first'>&laquo;</a>
				<a href="javascript:;" data-p='pre'>&lt;</a>
				<span>
				
				</span>
				<a href="javascript:;" data-p='next'>&gt;</a>
				<a href="javascript:;" data-p='last'>&raquo;</a>
			</div>
		</div>
		<div class="cmt-list">
		</div>
		<div class="cmt-new-post">
			<form action="" id="frm-new-post">
				<div class="new-post-avatar">
					<img src="${staticResourceRoot}/images/icon/avatar-default.jpg"/>
				</div>	
				<div class="new-post-body">
					<div class="new-post-message">
						<textarea placeholder="Write a comment ..." maxlength="500" required onkeyup="textAreaAdjust(this)" ></textarea>
					</div>
					<div style="min-height: 16px;">
						<span class="new-post-note">Remaining characters: <span>500<span></span>
						<span class="new-post-error">Bạn chưa đăng nhập.</span>
						<span class="new-post-success">Thành công.</span>
					</div>
				</div>	
			</form>
		</div>
	</section>
</body>
</html>

<#-- 
<#list LIST_COMMENTS as comment>
	<article class="cmt-item clearfix" data-user-name="{comment.account_name}">  
		<div class="cmt-avatar">
			<img src="${staticResourceRoot}/images/icon/avatar-default.jpg"/>
		</div>
		<div class="cmt-body">
			<span class="cmt-user-name">
				<a href="#">{comment.account_name}</a> :
			</span>
			<span class="cmt-content">{comment.content}</span>
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
				<span><a href="javascript:;" class="cmt-reply">Reply</a></span>
				<span class="cmt-time timeago" title="July 17, 2008">{comment.created?datetime?string("EEEE dd-MM-yyyy 'at' hh:mm")}</span>
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
					<span class="reply-post-success">Thành công.</span>
				</div>
			</div>	
		</div>
	</article>
</#list>
-->