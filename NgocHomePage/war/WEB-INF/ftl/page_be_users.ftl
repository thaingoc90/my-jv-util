<#import "inc_layout.ftl" as layout />
<@layout.masterTemplate title="Dashboard">

<div class="body-content">
	<header class="title-large">List Users </header>
	<div>
		<#if msgError??>
			<span class="dialog error">
				${msgError}
			</span>	
		</#if>
		<#if msgSuccess??>
			<span class="dialog success">
				${msgSuccess}
			</span>	
		</#if>
	</div>
	
	<button class="btn btn-info create-btn" data-toggle="modal" data-target="#createModal"><i class="icon-plus"></i> Create User</button>
	<#if USERS??>
	<table class="table table-hover">
		<tr>
			<th width="20">STT</th>
			<th>Email</th>
			<th width="100">Login Name</th>
			<th width="150">Display Name</th>
			<th width="60" class="ta-cen">Status</th>
			<th width="120">Group</th>
			<th width="80"></th>
		</tr>
		<#list USERS as user>
			<tr>
				<td>${user_index + 1}</td>
				<td><span class="user-email">${user.getEmail()}</span></td>
				<td><span class="user-name">${user.getLoginName()}</span></td>
				<td><span class="user-name">${user.getDisplayName()!""}</span></td>
				<td class="ta-cen"><#if !user.isLocked()><i class="icon-ok"></i></#if></td>
				<td><span class="user-group" style="display: none">${user.getGroupId()?c}</span>${GROUP_MAPPING[user.getGroupId()?string]!"No Group"}</td>
				<td>
					<#-- <#if user.getId() != 1> -->
						<a href="javascript:;" id="id${user_index}" class="modal-edit-open" rel="${user.getId()}" title="Edit"><i class="icon-pencil"></i></a>
						<a href="${baseUrl}users/lock/${user.getId()}" <#if user.isLocked()>title="Unlock"> <i class="icon-unlock"></i><#else>title="Lock"> <i class="icon-lock"></i></#if></a>
						<a href="javascript:;" class="modal-change-open" rel="${user.getId()}" title="Reset password"><i class="icon-repeat"> </i></a>
						<a href="#deleteModal" class="modal-delete-open" data-toggle="modal" data-href="${baseUrl}users/delete/${user.getId()}" title="Delete"><i class="icon-trash"> </i></a>
					<#-- </#if> -->
				</td>
			</tr>
		</#list>
	</table>
	</#if>
</div>

<div id="createModal" class="modal hide fade" >
	<form action="${baseUrl}users/add" method="post" class="form-horizontal">
		<div class="modal-header">
	    	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    		<div class="title-medium">Create User</div>
	  	</div>
		<div class="modal-body">
			<div class="control-group">
		    	<label class="control-label" for="inputEmail">Email <span class="require">*</span></label>
		    	<div class="controls">
		      		<input type="text" id="inputEmail" name="email" required placeholder="Email">
		    	</div>
		  	</div>
			<div class="control-group">
				<label class="control-label" for="inputName">Login Name <span class="require">*</span></label>
			    <div class="controls">
			      <input type="text" id="inputName" name="loginName" required placeholder="Login name">
			    </div>
		  	</div>
		  	<div class="control-group">
				<label class="control-label" for="displayName">Display Name</label>
			    <div class="controls">
			      <input type="text" id="displayName" name="displayName" placeholder="Login name">
			    </div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label" for="inputPassword">Password <span class="require">*</span></label>
		    	<div class="controls">
		      		<input type="password" id="inputPassword" name="password" required placeholder="Password">
		    	</div>
		  	</div>
		  	<div class="control-group">
			    	<label class="control-label">Group <span class="require">*</span></label>
			    	<div class="controls">
			      		<select  id="inputGroup" name="groupId">
			      			<#list GROUPS as group>
							  	<option value="${group.getId()}">${group.getName()}</option>
						  	</#list>
						</select>
			    	</div>
			  	</div>
		</div>
		<div class="modal-footer">
			<button type="submit" class="btn btn-success">Create</button>
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