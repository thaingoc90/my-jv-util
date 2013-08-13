<#import "inc_layout.ftl" as layout />
<@layout.masterTemplate title="Manage Permissions">

<div class="body-content">
	<header class="title-large">List Permissions </header>
	<#include "inc_error_zone.ftl" />
	
	<button class="btn btn-info create-btn" data-toggle="modal" data-target="#createModal"><i class="icon-plus"></i> Create Permission</button>
	
	<#if PERMISSIONS??>
	<table class="table table-hover">
		<tr>
			<th width="20">STT</th>
			<th width="220">Id</th>
			<th>Description</th>
			<th width="180">Parent Id</th>
			<th width="60"></th>
		</tr>
		<#list PERMISSIONS as perm>
		<tr>
			<td>${perm_index + 1}</td>
			<td><span class="perm-id <#if perm.getPid()??>pad-lef-20</#if>">${perm.getId()}</span></td>
			<td><span class="perm-desc">${perm.getDesc()}</span></td>
			<td><span class="perm-parent-id"><#if perm.getPid()??>${perm.getPid()}</#if></span></td>
			<td>
				<a href="#editModal" class="modal-edit-permission" data-toggle="modal" data-id="${perm.getId()}" title="Edit"><i class="icon-pencil"></i></a>
				<a href="#deleteModal" class="modal-delete-open" data-toggle="modal" data-href="${baseUrl}manage/permissions/delete/${perm.getId()}" title="Delete"><i class="icon-trash"></i></a>
			</td>
		</tr>
		</#list>
	</table>
	</#if>	
</div>

<div id="createModal" class="modal hide fade modal-form" >
	<form action="${baseUrl}manage/permissions/add" method="post" class="form-horizontal">
		<div class="modal-header">
	    	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    		<div class="title-medium">Create Permission</div>
	  	</div>
		<div class="modal-body">
			<div class="control-group">
				<label class="control-label" for="inputId">Id <span class="require">*</span></label>
			    <div class="controls">
			      <input type="text" id="inputId" name="id" required placeholder="Id">
			    </div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label" for="inputDesc">Description</label>
		    	<div class="controls">
		      		<input type="text" id="inputDesc" name="desc" placeholder="Description">
		    	</div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label">Parent Id</label>
		    	<div class="controls">
		      		<select id="inputPid" name="pid">
		      			<option value="0">--Select parent--</option>
		      			<#list PERMISSIONS as perm>
						  	<option value="${perm.getId()}">${perm.getId()}</option>
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
	<form action="${baseUrl}manage/permissions/edit" method="post" class="form-horizontal">
		<div class="modal-header">
	    	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    		<div class="title-medium">Edit Permission</div>
	  	</div>
		<div class="modal-body">
			<div class="control-group">
				<label class="control-label" for="inputId">Id</label>
			    <div class="controls">
			      	<input type="hidden" id="inputId" name="id" disable placeholder="Id">
			      	<span class="span-in-control" id="labelId">Id</span>
			    </div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label" for="inputDesc">Description</label>
		    	<div class="controls">
		      		<input type="text" id="inputDesc" name="desc" placeholder="Description">
		    	</div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label">Parent Id</label>
		    	<div class="controls">
		      		<select id="inputPid" name="pid">
		      			<option value="0">--Select parent--</option>
		      			<#list PERMISSIONS as perm>
						  	<option value="${perm.getId()}">${perm.getId()}</option>
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