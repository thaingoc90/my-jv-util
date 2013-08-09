<div class="error-zone">
	<#if msgErrors??>
		<#list msgErrors as msg>
			<div class="alert alert-error">
				<button type="button" class="close" data-dismiss="alert">&times;</button>
				${msg}
			</div>	
		</#list>
	</#if>
	<#if msgInfos??>
		<#list msgInfos as msg>
			<div class="alert alert-info">
				<button type="button" class="close" data-dismiss="alert">&times;</button>
				${msg}
			</div>
		</#list>	
	</#if>
	<#if msgSuccess??>
		<div class="alert alert-success">
			<button type="button" class="close" data-dismiss="alert">&times;</button>
			${msgSuccess}
		</div>
	</#if>
</div>