<#import "inc_layout.ftl" as layout />
<@layout.masterTemplate title="Comment Token" customJsList=["js/comment_add_domain"]>

<div class="body-content">
	<header class="title-large">List Tokens </header>
	<#include "inc_error_zone.ftl" />	
	<button class="btn btn-info create-btn" data-toggle="modal" data-target="#createModal"><i class="icon-plus"></i> New Token</button>
	<#if TOKENS??>
	<table class="table table-hover">
		<tr>
			<th width="20">STT</th>
			<th width="120" >Token</th>
			<th width="100" class="ta-cen">Type</th>
			<th>Domains</th>
			<th width="160">Created</th>
			<th width="40"></th>
		</tr>
		<#list TOKENS as token>
			<tr>
				<td>${token_index + 1}</td>
				<td><span class="token">${token.token}</span></td>
				<td class="ta-cen"><span class="comment_type">${token.comment_type}</span></td>
				<td>
					<#list token.list_domains as domain>
						<p>${domain}</p>
					</#list>
					<span class="target_domains hide">
						${token.target_domains}
					</span>
				</td>
				<td><span class="created">${token.created?string("HH:mm:ss dd/MM/yyyy")}</span></td>
				<td>
					<a href="#editModal" class="modal-edit-token" data-toggle="modal" data-id="${token.token}" title="Edit"><i class="icon-pencil"></i></a>
					<a href="#deleteModal" class="modal-delete-open" data-toggle="modal" data-href="${baseUrl}comment/token/delete/${token.token}" title="Delete"><i class="icon-trash"></i></a>
				</td>
			</tr>
		</#list>
	</table>
	</#if>
</div>

<div id="createModal" class="modal hide fade modal-form" style="width: 470px;">
	<form action="${baseUrl}comment/token/add" method="post" class="form-horizontal">
		<div class="modal-header">
	    	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    		<div class="title-medium">Create Token</div>
	  	</div>
		<div class="modal-body">
			<div class="control-group">
		    	<label class="control-label" for="inputEmail">Token <span class="require">*</span></label>
		    	<div class="controls">
		      		<input type="text" id="inputToken" name="token" required placeholder="Token">
		    	</div>
		  	</div>
			<div class="control-group">
				<label class="control-label" for="inputLoginName">Comment Type</label>
			    <div class="controls">
			    	<select  id="inputCommentType" name="commentType">
			    		<#list commentTypes?keys as type>
						  	<option value="${type}">${commentTypes[type]}</option>
					  	</#list>
					</select>
			    </div>
		  	</div>
		  	<div class="control-group">
				<label class="control-label" for="inputDisplayName">Domains <span class="require">*</span></label>
			    <div class="controls">
			    	<div class="list-domains">
				    	<div class="target-domains" >
				      		<input type="text" name="targetDomains" required placeholder="Domain">
				      	</div>
			      	</div>
			      	<a href="javascript:;" class="add-domain">Add Domain</a>
			    </div>
		  	</div>
		</div>
		<div class="modal-footer">
			<button type="submit" class="btn btn-success">Create</button>
	   		<button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
	 	</div>
 	</form>
</div>

<div id="editModal" class="modal hide fade modal-form" >
	<form action="${baseUrl}comment/token/edit" method="post" class="form-horizontal">
		<div class="modal-header">
	    	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    		<div class="title-medium">Edit Token</div>
	  	</div>
		<div class="modal-body">
			<div class="control-group">
		    	<label class="control-label" for="inputToken">Token <span class="require">*</span></label>
		    	<div class="controls">
		      		<input type="hidden" id="inputToken" name="token" required>
		      		<span class="text-info span-in-control"></span>
		    	</div>
		  	</div>
			<div class="control-group">
				<label class="control-label" for="inputLoginName">Comment Type</label>
			    <div class="controls">
			    	<select  id="inputCommentType" name="commentType">
			    		<#list commentTypes?keys as type>
						  	<option value="${type}">${commentTypes[type]}</option>
					  	</#list>
					</select>
			    </div>
		  	</div>
		  	<div class="control-group">
				<label class="control-label" for="inputDisplayName">Domains <span class="require">*</span></label>
			    <div class="controls">
			    	<div class="list-domains">
				    	<div class="target-domains" >
				      		<input type="text" name="targetDomains" required placeholder="Domain">
				      	</div>
			      	</div>
			      	<a href="javascript:;" class="add-domain">Add Domain</a>
			    </div>
		  	</div>
		</div>
		<div class="modal-footer">
			<button type="submit" class="btn btn-success">Save</button>
	   		<button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
	 	</div>
 	</form>
</div>
	
<div id="deleteModal" class="modal hide fade" tabindex="-1" role="dialog">
	<div class="modal-body">
		<p>Are you sure you want to delete?</p>
	</div>
	<div class="modal-footer">
		<a href="#" class="btn btn-success">Yes</a>
   		<button class="btn" data-dismiss="modal" aria-hidden="true">No</button>
 	</div>
</div>
</@layout.masterTemplate>