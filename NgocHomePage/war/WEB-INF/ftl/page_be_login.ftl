<#import "inc_layout.ftl" as layout />
<@layout.masterTemplate title="Login" isLogin=false>
<style>
	body {background-color: #ccc;overflow: hidden;}
	.error-zone{padding-right: 53px;}
</style>

<section id="login-form">
	<form class="" action="/admin/login" id="frmLogin" method="post" >
		<fieldset>
			<div class="title-large">Sign In</div>
			<#include "inc_error_zone.ftl" />
				
			<div class="input-prepend">
				<span class="add-on"><i class="icon-envelope"></i></span>
				<input type="text" id="inputEmail" name="email" required placeholder="Email">
			</div>
			<div class="input-prepend">
				<span class="add-on"><i class="icon-key"></i></span>
				<input type="password" id="inputPassword" name="password" required placeholder="Password">
			</div>
			<label class="checkbox">
		    	<input type="checkbox"> Remember me
		    	| <a href="${baseUrl}forgotpassword">Forgot password?</a>
		    </label>
		    <div style="margin-top: 10px;">
				<button type="submit" class="btn btn-small btn-success">Sign In</button>
			</div>
	  	</fieldset>
	</form>
</section>

</@layout.masterTemplate>