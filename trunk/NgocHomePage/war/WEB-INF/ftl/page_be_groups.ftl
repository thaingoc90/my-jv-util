<#import "inc_layout.ftl" as layout />
<@layout.masterTemplate title="Manage Groups">

<div class="body-content">
	<header class="title-large">List Groups </header>
	<#include "inc_error_zone.ftl" />
	
	<button class="btn btn-info create-btn" data-toggle="modal" data-target="#createModal"><i class="icon-plus"></i> Create Group</button>
	<#if GROUPS??>
	<table class="table table-hover">
		<tr>
			<th width="20">STT</th>
			<th width="150">Name</th>
			<th>Description</th>
			<th width="80" class="ta-cen">System</th>
			<th width="80"></th>
		</tr>
		<#list GROUPS as group>
			<tr <#if group.getId() == 1>class="info"</#if>>
				<td>${group_index + 1}</td>
				<td><span class="group-name">${group.getName()}</span></td>
				<td><span class="group-desc">${group.getDesc()}</span></td>
				<td class="ta-cen"><span class="group-system"><#if group.isSystem()><i class="icon-ok"></i></#if></span></td>
				<td>
					<#if group.getId() != 1>
						<a href="#editModal" class="modal-edit-group" data-toggle="modal" data-id="${group.getId()}" title="Edit"><i class="icon-pencil"></i></a>
						<a href="#permMappingModel" class="modal-permission-mapping" data-toggle="modal" data-id="${group.getId()}" data-group-name="${group.getName()}" title="Permission"><i class="icon-list"> </i></a>
						<a href="#deleteModal" class="modal-delete-open" data-toggle="modal" data-href="${baseUrl}manage/groups/delete/${group.getId()}" title="Delete"><i class="icon-trash"></i></a>
					</#if>
				</td>
			</tr>
		</#list>
	</table>
	</#if>	
</div>

<div id="createModal" class="modal hide fade modal-form" >
	<form action="${baseUrl}manage/groups/add" method="post" class="form-horizontal">
		<div class="modal-header">
	    	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    		<div class="title-medium">Create Group</div>
	  	</div>
		<div class="modal-body">
			<div class="control-group">
				<label class="control-label" for="inputName">Name <span class="require">*</span></label>
			    <div class="controls">
			      <input type="text" id="inputName" name="name" required placeholder="Name">
			    </div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label" for="inputDesc">Description</label>
		    	<div class="controls">
		      		<input type="text" id="inputDesc" name="desc" placeholder="Description">
		    	</div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label">System</label>
		    	<div class="controls">
		    		<label class="checkbox">
			      		<input type="checkbox" name="system">
			      		<small>(Group will have all permissions)</small>
		      		</label>
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
	<form action="${baseUrl}manage/groups/edit" method="post" class="form-horizontal">
		<div class="modal-header">
	    	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    		<div class="title-medium">Edit Group</div>
	  	</div>
		<div class="modal-body">
			<input type="hidden" id="inputId" name="id" required>
			<div class="control-group">
				<label class="control-label" for="inputName">Name</label>
			    <div class="controls">
			      <input type="text" id="inputName" name="name" required="required" placeholder="Name" />
			    </div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label" for="inputDesc">Description</label>
		    	<div class="controls">
		      		<input type="text" id="inputDesc" name="desc" placeholder="Description" />
		    	</div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label">System</label>
		    	<div class="controls">
		      		<input type="checkbox" id="inputSystem" name="system"/>
		    	</div>
		  	</div>
		</div>
		<div class="modal-footer">
			<button type="submit" class="btn btn-success">Save</button>
	   		<button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
	 	</div>
 	</form>
</div>

<div id="permMappingModel" class="modal hide fade modal-form">
	<form action="${baseUrl}manage/groups/permission" method="post" class="form-horizontal">
		<div class="modal-header">
	    	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    		<div class="title-medium">Edit Group</div>
	  	</div>
	  	<div class="modal-body">
			<input type="hidden" id="inputId" name="id" required>
			<div class="control-group">
				<label class="control-label">Group</label>
			    <div class="controls">
			     	<span class="text-info span-in-control" id="inputName">Group</span>
			    </div>
		  	</div>
		  	<div class="control-group">
				<label class="control-label" for="inputPermission">Permissions</label>
			    <div class="controls" style="padding-top: 5px;">
			    	<#if PERMISSIONS??>
			    	
			    	<#list PERMISSIONS as permission>
					  	<#if !permission.getPid()?? || permission.getPid() == "">
					  	<div>
					  		<label class="checkbox">
					  			<input type="checkbox" value="${permission.getId()}" id="${permission.getId()}" name="permissions"/> ${permission.getId()}
					  		</label>
					  		<div class="pad-lef-20">
					  		<#list PERMISSIONS as subPerm>
								<#if subPerm.getPid()??	&& subPerm.getPid()	== permission.getId()>
									<label class="checkbox">
							  			<input type="checkbox" value="${subPerm.getId()}" id="${subPerm.getId()}" name="permissions"/> ${subPerm.getId()}
							  		</label>
								</#if>		  			
					  		</#list>
					  		</div>
					  	</div>
					  	</#if>
				  	</#list>
				  	
					</#if>
			    </div>
		  	</div>
		</div>
		<div class="modal-footer">
			<button type="submit" class="btn btn-success">Save</button>
	   		<button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
	 	</div>
	</form>
</div>

<div id="deleteModal" class="modal hide fade">
	<div class="modal-body">
		<p>Are you sure you want to delete?</p>
	</div>
	<div class="modal-footer">
		<a href="#" class="btn btn-success">Yes</a>
   		<button class="btn" data-dismiss="modal" aria-hidden="true">No</button>
 	</div>
</div>


<script>
	var gpMapping = ${GPMAPPING};
</script>

</@layout.masterTemplate>