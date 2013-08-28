<#import "inc_layout.ftl" as layout />
<@layout.masterTemplate title="Dashboard" customJsList=["js/url"]>
<script>
	var url = removeParameterFromCurUrl('jsession');
	history.replaceState({ path: url }, '', url);
</script>

<div class="row-fluid wrapper">
	<h1>Dashboard</h1>
</div>

</@layout.masterTemplate>