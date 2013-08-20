<#import "inc_layout.ftl" as layout />
<@layout.masterTemplate title="Profile" customCssList=['bootstrap2.3.2/datepicker/css/datepicker'] customJsList=['bootstrap2.3.2/datepicker/js/bootstrap-datepicker']>
<style>
	.profile li {margin-bottom: 5px;} 
	.well {min-height: 380px !important;margin-right: 50px;}
	#inputBirthday {cursor: default;}
</style>

<div class="body-content">
<#if USER??>
	<div class="row-fluid" style="margin-top: 50px;">
		<div class="span5 well">
			<header class="title-medium">Profile</header>
			<div class="seperatorLine"></div>
			<#include "inc_error_zone.ftl" />
			<form id="formProfile" method="post">
				<ul class="profile form-inline">
					<li>Email: <a href="mailto:${USER.getEmail()}"><span class="label label-info">${USER.getEmail()}</span></a></li>
					<li>Login Name: <span class="label label-warning">${USER.getLoginName()}</span></li>
					<li>Group: <span class="label">${GRMapping[USER.getGroupId()?string]}</span></li>
					<li>Password: <a href="${baseUrl}profile/changePassword">Change password</a></li>
					<li>Register: ${USER.getCreatedDate()?string("dd/MM/yyyy")}</li>
					<li>Last Login: ${USER.getLastLogin()?string("dd/MM/yyyy")!""}</li>
					<li>
						<label>Display Name:</label> 
						<span id="label-name">${USER.getDisplayName()}</span> 
						<input type="text" class="span7 hide" id="inputName" name="displayName" value="${USER.getDisplayName()}" />
					</li>
					<li>
						<label>Birthday:</label>
						<span id="label-birthday"><#if USER.getBirthday()??>${USER.getBirthday()?string("dd/MM/yyyy")}</#if></span> 
						<div class="input-append date hide" id="birthday-picker" data-date="<#if USER.getBirthday()??>${USER.getBirthday()?string('dd-MM-yyyy')}</#if>" data-date-format="dd-mm-yyyy">
							<input type="text" class="span7" id="inputBirthday" name="birthday" readonly value="<#if USER.getBirthday()??>${USER.getBirthday()?string('dd-MM-yyyy')}</#if>" />
							<span class="add-on"><i class="icon-th"></i></span>
						</div>
					</li>
				</ul>
				<div>
					<button type="button" class="btn btn-small btn-info" id="edit-btn">Edit</button>
					<button type="submit" class="btn btn-small btn-success hide" id="save-btn">Save</button>
			  		<button type="button" class="btn btn-small hide" id="back-btn">Back</button>
				</div>
			</form>
		</div>
		<div class="span5 well">
			<header class="title-medium">History</header>
			<div class="seperatorLine"></div>
		</div>
	</div>
</#if>
</div>

<script type="text/javascript">
	 $('#birthday-picker').datepicker({ dateFormat: "dd-mm-yyyy"});
	 
	 $(document).on('click', '#edit-btn', function() {
	 	$('#label-birthday').addClass('hide');
	 	$('#birthday-picker').removeClass('hide');
	 	$('#label-name').addClass('hide');
	 	$('#inputName').removeClass('hide');
		$('#edit-btn').hide();
	 	$('#save-btn').show();
	 	$('#back-btn').show();
	 });
	 
	  $(document).on('click', '#back-btn', function() {
	  	$('#label-birthday').removeClass('hide');
	 	$('#birthday-picker').addClass('hide');
	 	$('#label-name').removeClass('hide');
	 	$('#inputName').addClass('hide');
		$('#edit-btn').show();
	 	$('#save-btn').hide();
	 	$('#back-btn').hide();
	  });
</script>

</@layout.masterTemplate>