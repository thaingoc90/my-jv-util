<#import "inc_layout.ftl" as layout />
<@layout.masterTemplate title="Manage MenuItems">


<div class="body-content">
	<header class="title-large">List Menu </header>
	<#include "inc_error_zone.ftl" />	

	<button class="btn btn-info create-btn" data-toggle="modal" data-target="#createModal"><i class="icon-plus"></i> Create Menu Item</button>
	
	<#if MENUS??>
	<table class="table table-hover">
		<tr>
			<th width="20">STT</th>
			<th width="140">Name</th>
			<th>Url</th>
			<th width="20" class="ta-cen">Position</th>
			<th width="110">Parent-Menu</th>
			<th width="180">Permission</th>
			<th width="40"></th>
		</tr>
		<#assign count = 0>
		<#list MENUS as menu>
		<tr>
			<#assign count = count + 1>
			<td>${count}</td>
			<td><span class="menu-name">${menu.getName()}</span></td>
			<td><span class="menu-url">${menu.getUrl()}</span></td>
			<td class="menu-position ta-cen"><span>${menu.getPosition()}</span></td>
			<td><span class="menu-parent hide"></span></td>
			<td><span class="menu-permission"><#if menu.getPermission()??>${menu.getPermission()}</#if></span></td>
			<td>
				<a href="#editModal" class="modal-edit-menu" data-toggle="modal" data-id="${menu.getId()}" title="Edit"><i class="icon-pencil"></i></a>
				<a href="#deleteModal" class="modal-delete-open" data-toggle="modal" data-href="${baseUrl}manage/menus/delete/${menu.getId()}" title="Delete"><i class="icon-trash"></i></a>
			</td>
		</tr>
		<#if menu.getChilds()??>
			<#list menu.getChilds() as subMenu>
			<tr>
				<#assign count = count + 1>
				<td>${count}</td>
				<td><span class="menu-name pad-lef-20">${subMenu.getName()}</span></td>
				<td><span class="menu-url">${subMenu.getUrl()}</span></td>
				<td class="ta-cen"><span class="menu-position">${subMenu.getPosition()}</span></td>
				<td><span class="menu-parent hide">${menu.getId()}</span>${menu.getName()}</td>
				<td><span class="menu-permission"><#if subMenu.getPermission()??>${subMenu.getPermission()}</#if></span></td>
				<td>
					<a href="#editModal" class="modal-edit-menu" data-toggle="modal" data-id="${subMenu.getId()}" title="Edit"><i class="icon-pencil"></i></a>
					<a href="#deleteModal" class="modal-delete-open" data-toggle="modal" data-href="${baseUrl}manage/menus/delete/${subMenu.getId()}" title="Delete"><i class="icon-trash"></i></a>
				</td>
			</tr>
			</#list>
		</#if>
		</#list>
	</table>
	</#if>
</div>

<div id="createModal" class="modal hide fade modal-form" >
	<form action="${baseUrl}manage/menus/add" method="post" class="form-horizontal">
		<div class="modal-header">
	    	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    		<div class="title-medium">Create Menu Item</div>
	  	</div>
		<div class="modal-body">
			<div class="control-group">
				<label class="control-label" for="inputName">Name <span class="require">*</span></label>
			    <div class="controls">
			      <input type="text" id="inputName" name="name" required placeholder="Name">
			    </div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label" for="inputUrl">Url</label>
		    	<div class="controls">
		      		<input type="text" id="inputUrl" name="url" placeholder="Url">
		    	</div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label" for="inputPosition">Position</label>
		    	<div class="controls">
		      		<input type="number" id="inputPosition" name="position" placeholder="Position">
		    	</div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label">Parent Menu</label>
		    	<div class="controls">
		      		<select id="inputPid" name="parentId">
		      			<option value="0">--Select parent--</option>
		      			<#if MENUS??>
		      			<#list MENUS as menu>
						  	<option value="${menu.getId()}">${menu.getName()}</option>
					  	</#list>
					  	</#if>
					</select>
		    	</div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label">Permission</label>
		    	<div class="controls">
		      		<select id="inputPermission" name="permission">
		      			<option value="0">--Select permission--</option>
		      			<#if PERMISSIONS??>
		      			<#list PERMISSIONS as perm>
						  	<option value="${perm.getId()}">${perm.getId()}</option>
					  	</#list>
					  	</#if>
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
	<form action="${baseUrl}manage/menus/edit" method="post" class="form-horizontal">
		<div class="modal-header">
	    	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    		<div class="title-medium">Edit Menu Item</div>
	  	</div>
		<div class="modal-body">
			<div class="control-group">
				<label class="control-label" for="inputName">Name <span class="require">*</span></label>
			    <div class="controls">
			    	<input type="hidden" id="inputId" name="id">
			      	<input type="text" id="inputName" name="name" required placeholder="Name">
			    </div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label" for="inputUrl">Url</label>
		    	<div class="controls">
		      		<input type="text" id="inputUrl" name="url" placeholder="Url">
		    	</div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label" for="inputPosition">Position</label>
		    	<div class="controls">
		      		<input type="number" id="inputPosition" name="position" placeholder="Position">
		    	</div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label">Parent Menu</label>
		    	<div class="controls">
		      		<select id="inputParentId" name="parentId">
		      			<option value="0">--Select parent--</option>
		      			<#if MENUS??>
		      			<#list MENUS as menu>
						  	<option value="${menu.getId()}">${menu.getName()}</option>
					  	</#list>
					  	</#if>
					</select>
		    	</div>
		  	</div>
		  	<div class="control-group">
		    	<label class="control-label">Permission</label>
		    	<div class="controls">
		      		<select id="inputPermission" name="permission">
		      			<option value="0">--Select permission--</option>
		      			<#if PERMISSIONS??>
		      			<#list PERMISSIONS as perm>
						  	<option value="${perm.getId()}">${perm.getId()}</option>
					  	</#list>
					  	</#if>
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

<div id="deleteModal" class="modal hide fade">
	<div class="modal-body">
		<p>Are you sure you want to delete?</p>
	</div>
	<div class="modal-footer">
		<a href="#" class="btn btn-success">Yes</a>
   		<button class="btn" data-dismiss="modal" aria-hidden="true">No</button>
 	</div>
</div>
</@layout.masterTemplate>