<#if pagination?? && pagination.getTotalPage() &gt; 1>
	<div class="pagination pagination-right">
	  <ul>
	  	<li><a href="${pagination.getUrlForPage(1)}">&laquo;</a></li>
	  	<#if pagination.getCurrentPage() &gt; 1>
	  		<li><a href="${pagination.getUrlForPage(pagination.getPreviousPage())}">&lt;</a></li>
	  	<#else>
			<li class="disabled"><a>&lt;</a></li>	  		
	  	</#if>
	    <#list pagination.getVisiblePages() as page>
	    	<#if page == pagination.getCurrentPage()>
	    		<li class="active"><a>${page?c}</a></li>
	    	<#else>
	    		<li><a href="${pagination.getUrlForPage(page)}">${page?c}</a></li>
	    	</#if>
	    </#list>
	    <#if pagination.getCurrentPage() &lt; pagination.getTotalPage()>
	  		<li><a href="${pagination.getUrlForPage(pagination.getNextPage())}">&gt;</a></li>
	  	<#else>
			<li class="disabled"><a>&gt;</a></li>	  		
	  	</#if>
	    <li><a href="${pagination.getUrlForPage(pagination.getTotalPage())}">&raquo;</a></li>
	  </ul>
	</div>
</#if>
	