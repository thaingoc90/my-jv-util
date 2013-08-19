<#import "inc_layout.ftl" as layout />
<@layout.masterTemplate title="Profile">

<div class="body-content">
	<header class="title-large">Profile</header>
	<#include "inc_error_zone.ftl" />
	
	<#if USER??>
		<div>
			<dl class="dl-horizontal">
			  	<dt>Login Name:</dt>
				<dd>${USER.getLoginName()}</dd>
				<dt>Display Name:</dt>
				<dd>${USER.getDisplayName()}</dd>
			</dl>
		</div>
	</#if>
</div>
</@layout.masterTemplate>