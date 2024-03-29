<#import "inc_layout.ftl" as layout />
<@layout.masterTemplate title="Profile">

<div class="row-fluid wrapper">
	<div class="body-change-pass">
		<header class="text-center title-large">Change Password</header>
		
		<#include "inc_error_zone.ftl" />
		<form action="${baseUrl}profile/changePassword" method="post" class="form-horizontal" id="formChangePassword">
			<div class="control-group">
				<label class="control-label" for="inputOldPassword">Old Password <span class="require">*</span></label>
			    <div class="controls">
			      <input type="password" id="inputOldPassword" name="oldPassword" required placeholder="Old password">
			    </div>
		  	</div>
		  	<div class="control-group">
				<label class="control-label" for="inputPassword">New Password <span class="require">*</span></label>
			    <div class="controls">
			      <input type="password" id="inputPassword" name="password" required placeholder="New password">
			    </div>
		  	</div>
		  	<div class="control-group">
				<label class="control-label" for="inputConfirmPassword">Confirm Password <span class="require">*</span></label>
			    <div class="controls">
			      <input type="password" id="inputConfirmPassword" name="confirmPassword" required placeholder="Confirm password">
			    </div>
		  	</div>
		  	<div class="control-group text-center">
		  		<button type="submit" class="btn btn-info">Save</button>
		  		<button type="button" class="btn" onclick="toLink('${baseUrl}profile');">Cancel</button>
			</div>
	 	</form>
	</div>
</div>

</@layout.masterTemplate>