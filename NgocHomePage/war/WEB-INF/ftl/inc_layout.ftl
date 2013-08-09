<#macro masterTemplate title="Home" isLogin=true customCssList=[] customJsList=[]>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8" />
    <meta name="description" content="description" />
    <meta name="keywords" content="contents" />
    <meta name="author" content="SPINNING" />
    
    <title>Admin backend- ${title}</title>
    
    <link rel="stylesheet" type="text/css" href="${staticResourceRoot}bootstrap2.3.2/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="${staticResourceRoot}font-awesome/css/font-awesome.min.css"/>
    <link rel="stylesheet" type="text/css" href="${staticResourceRoot}css/be-base.css"/>
    <link rel="stylesheet" type="text/css" href="${staticResourceRoot}css/dialogbox.css"/>
    
    <#list customCssList as customCss>
		<link rel="stylesheet" type="text/css" href="${staticResourceRoot}${customCss}.css"/>
	</#list>
    
	<script type="text/javascript">
		var baseUrl = '<#if baseUrl??>${baseUrl}</#if>';
	</script>
</head>
<body>
	<!-- MENU -->
	<#if isLogin>
		<#include "inc_menu.ftl" />	
	<#else>
	</#if>
	
	<!-- CONTENT -->
	<section class="container" id="content">	
		<#nested />
	</section>
	
	<script type="text/javascript" src="${staticResourceRoot}js/jquery-2.0.2.min.js" charset="utf-8"></script>
    <script type="text/javascript" src="${staticResourceRoot}bootstrap2.3.2/js/bootstrap.min.js" charset="utf-8"></script>
    <script type="text/javascript" src="${staticResourceRoot}js/be-main.js" charset="utf-8"></script>
    <#list customJsList as customJs>
		<script type="text/javascript" src="${staticResourceRoot}${customJs}.js"></script>
	</#list>
</body>
</#macro>