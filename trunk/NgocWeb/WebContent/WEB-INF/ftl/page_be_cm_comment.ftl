<#import "inc_layout.ftl" as layout />
<@layout.masterTemplate title="Comment" customJsList=["js/date.format"]>

<div class="body-content" style="width: 95%;" data-token="${token}">
	<header class="title-large">List Comments of '${token}' </header>
	<button class="btn btn-info create-btn" onclick="location.reload();"><i class="icon-refresh"></i> Refresh</button>
	<#include "inc_error_zone.ftl" />	
	<#if COMMENTS??>
	<table class="table table-hover">
		<tr>
			<th width="20">STT</th>
			<th>Target</th>
			<th width="100">User</th>
			<th width="50" class="ta-cen">Status</th>
			<th width="400">Content</th>
			<th width="100">Parent</th>
			<th width="140">Created</th>
			<th width="60"></th>
		</tr>
		<#list COMMENTS as comment>
			<tr class="<#if comment.status==0>info<#elseif comment.status==2>error</#if>">
				<td>
					<a href="#detailModal" class="modal-detail-comment" data-toggle="modal" data-comment-id="${comment.comment_id}" data-target-id="${comment.target_id}">${comment_index + 1}</a>
					<span class="comment_id hide">${comment.comment_id}</span></td>
				<td>
					<span class="target">${comment.target}</span>
					<span class="target_id hide">${comment.target_id}</span>
				</td>
				<td><span class="account-name">${comment.account_name}</span></td>
				<td class="ta-cen">
					<#if comment.status==1><i class="icon-ok"></i><#elseif comment.status==2><i class="icon-remove"></i></#if>
					<span class="status hide">${comment.status}</span>
				</td>
				<td>
					<#if comment.content?length &lt; 60>${comment.content}<#else>${comment.content?substring(0,57)}...</#if>
					<span class="content hide">${comment.content}</span>
				</td>
				<td>
					<#if comment.parent_comment_id??>
						<a href="#detailModal" class="modal-detail-comment" data-toggle="modal" data-comment-id="${comment.parent_comment_id}" data-target-id="${comment.target_id}">${comment.parent_comment_id}</a>
					</#if>
				</td>
				<td>${comment.created?string("HH:mm:ss dd/MM/yyyy")}</td>
				<td>
					<a href="#editModal" class="modal-edit-comment" data-toggle="modal" data-id="${comment.comment_id}" title="Edit"><i class="icon-pencil"></i></a>
					<a href="#deleteModal" class="modal-delete-open" data-toggle="modal" data-href="${baseUrl}comment/list/delete?token=${token}&comment_id=${comment.comment_id}&target_id=${comment.target_id}" title="Delete"><i class="icon-trash"></i></a>
					<#if comment.status!=2>
						<a href="${baseUrl}comment/list/reject?token=${token}&comment_id=${comment.comment_id}&target_id=${comment.target_id}" title="Reject"><i class="icon-remove-sign"></i></a>
					</#if>
					<#if comment.status!=1>
						<a href="${baseUrl}comment/list/approve?token=${token}&comment_id=${comment.comment_id}&target_id=${comment.target_id}" title="Approve"><i class="icon-ok-sign"></i></a>
					</#if>
				</td>
			</tr>
		</#list>
	</table>
	
	<#include "inc_pagination.ftl" />
	</#if>
</div>

<div id="editModal" class="modal hide fade modal-form" style="width: 520px;">
	<form action="${baseUrl}comment/list/edit" method="post" class="form-horizontal">
		<div class="modal-header">
	    	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    		<div class="title-medium">Edit Comment</div>
	  	</div>
		<div class="modal-body">
		  	<div class="control-group">
		    	<label class="control-label" for="inputUser">Account Name</label>
		    	<div class="controls">
		    		<input type="hidden" id="inputToken" name="token" required>
		    		<input type="hidden" id="inputCommentId" name="commentId" required>
		      		<input type="hidden" id="inputTargetId" name="targetId" required>
		      		<span  id="inputUser" class="text-info span-in-control"></span>
		    	</div>
		  	</div>
			<div class="control-group">
				<label class="control-label" for="inputStatus">Status</label>
			    <div class="controls">
			    	<select  id="inputStatus" name="status">
			    		<#list statusMap?keys as status>
						  	<option value="${status}">${statusMap[status]}</option>
					  	</#list>
					</select>
			    </div>
		  	</div>
		  	<div class="control-group">
				<label class="control-label" for="inputContent">Content <span class="require">*</span></label>
			    <div class="controls">
			    	<textarea id="inputContent" class="textarea-in-modal" name="content" required></textarea>
			    </div>
		  	</div>
		</div>
		<div class="modal-footer">
			<button type="submit" class="btn btn-success">Save</button>
	   		<button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
	 	</div>
 	</form>
</div>

<div id="detailModal" class="modal hide fade modal-form" style="width: 470px;">
	<div class="form-horizontal">
		<div class="modal-header">
	    	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<div class="title-medium">Detail Comment</div>
	  	</div>
		<div class="modal-body">
		  	<div class="control-group">
		    	<label class="control-label" for="inputUser">Account Name</label>
		    	<div class="controls">
		      		<span id="inputUser" class="text-info span-in-control"></span>
		    	</div>
		  	</div>
			<div class="control-group">
				<label class="control-label" for="inputStatus">Status</label>
			    <div class="controls">
			    	<span id="inputStatus" class="span-in-control"></span>
			    </div>
		  	</div>
		  	<div class="control-group">
				<label class="control-label" for="inputApprover">Approver</label>
			    <div class="controls">
			    	<span id="inputApprover" class="text-info span-in-control"></span>
			    </div>
		  	</div>
		  	<div class="control-group">
				<label class="control-label" for="inputUpdater">Updater</label>
			    <div class="controls">
			    	<span id="inputUpdater" class="text-info span-in-control"></span> 
			    	<span id="inputUpdateTime" class="span-in-control" style="font-weight: normal;"></span>
			    </div>
		  	</div>
		  	<div class="control-group">
				<label class="control-label" for="inputCreateTime">CreateTime</label>
			    <div class="controls">
			    	<div id="inputCreateTime" class="span-in-control" name="content" style="font-weight: normal;"></div>
			    </div>
		  	</div>
		  	<div class="control-group">
				<label class="control-label" for="inputContent">Content</label>
			    <div class="controls">
			    	<div id="inputContent" class="span-in-control" name="content" style="font-weight: normal;"></div>
			    </div>
		  	</div>
		</div>
		<div class="modal-footer">
	   		<button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
	 	</div>
	 </div>
</div>

<div id="deleteModal" class="modal hide fade" tabindex="-1" role="dialog">
	<div class="modal-body">
		<p>Are you sure you want to delete? All of comment's childs will be so deleted!</p>
	</div>
	<div class="modal-footer">
		<a href="#" class="btn btn-success">Yes</a>
   		<button class="btn" data-dismiss="modal" aria-hidden="true">No</button>
 	</div>
</div>
</@layout.masterTemplate>