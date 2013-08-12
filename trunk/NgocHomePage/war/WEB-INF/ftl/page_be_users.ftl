<#import "inc_layout.ftl" as layout />
<@layout.masterTemplate title="Dashboard">

<div class="body-content">
	<header class="title-large">List Users </header>
	<#include "inc_error_zone.ftl" />	

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
			<th width="60"></th>
		</tr>
		<#list USERS as user>
			<tr <#if user.getId() == 1>class="info"</#if>>
				<td>${user_index + 1}</td>
				<td><span class="user-email">${user.getEmail()}</span></td>
				<td><span class="user-login-name">${user.getLoginName()}</span></td>
				<td><span class="user-display-name">${user.getDisplayName()!""}</span></td>
				<td class="ta-cen"><#if !user.isLocked()><i class="icon-ok"></i></#if></td>
				<td><span class="user-group hide">${user.getGroupId()?c}</span>${GROUP_MAPPING[user.getGroupId()?string]!"No Group"}</td>
				<td>
					<#if user.getId() != 1>
						<a href="#editModal" class="modal-edit-user" data-toggle="modal" data-id="${user.getId()}" title="Edit"><i class="icon-pencil"></i></a>
						<a href="${baseUrl}users/lock/${user.getId()}" <#if user.isLocked()>title="Unlock"> <i class="icon-unlock"></i><#else>title="Lock"> <i class="icon-lock"></i></#if></a>
						<a href="#deleteModal" class="modal-delete-open" data-toggle="modal" data-href="${baseUrl}users/delete/${user.getId()}" title="Delete"><i class="icon-trash"></i></a>
					</#if>
				</td>
			</tr>
		</#list>
	</table>
	</#if>
</div>

<div id="createModal" class="modal hide fade modal-form" >
	<form action="${baseUrl}users/add" method="post" class="form-horizontal">
		<div class="modal-header">
	    	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    		<div class="title-medium">Create User</div>
	  	</div>
		<div class="modal-body">
			<div class="control-group">
		    	<label class="control-label" for="inputEmail">Email <span class="require">*</span></label>
		    	<div class="controls">
		      		<input type="email" id="inputEmail" name="email" required placeholder="Email">
		    	</div>
		  	</div>
			<div class="control-group">
				<label class="control-label" for="inputLoginName">Login Name <span class="require">*</span></label>
			    <div class="controls">
			      <input type="text" id="inputLoginName" name="loginName" required placeholder="Login name">
			    </div>
		  	</div>
		  	<div class="control-group">
				<label class="control-label" for="inputDisplayName">Display Name</label>
			    <div class="controls">
			      <input type="text" id="inputDisplayName" name="displayName" placeholder="Login name">
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

<div id="editModal" class="modal hide fade modal-form" >
	<form action="${baseUrl}users/edit" method="post" class="form-horizontal">
		<div class="modal-header">
	    	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    		<div class="title-medium">Edit User</div>
	  	</div>
		<div class="modal-body">
			<input type="hidden" id="inputId" name="id" required>
			<div class="control-group">
		    	<label class="control-label">Email</label>
		    	<div class="controls">
		      		<span id="inputEmail" class="text-info span-in-control"></span>
		    	</div>
		  	</div>
			<div class="control-group">
				<label class="control-label">Login Name</label>
			    <div class="controls">
			     	<span id="inputLoginName" class="text-info span-in-control"></span>
			    </div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label" for="inputPassword">New Password</label>
		    	<div class="controls">
		      		<input type="password" id="inputPassword" name="password">
		      		<div><small>Input if want to change password</small></div>
		    	</div>
		  	</div>
		  	<div class="control-group">
				<label class="control-label" for="inputDisplayName">Display Name</label>
			    <div class="controls">
			      	<input type="text" id="inputDisplayName" name="displayName" value="" placeholder="Login name">
			    </div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label">Group </label>
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