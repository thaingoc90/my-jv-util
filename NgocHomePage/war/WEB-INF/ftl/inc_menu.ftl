<div class="navbar navbar-inverse navbar-fixed-top">
	<div class="navbar-inner">
           <div class="container">
             	<a class="brand" href="${baseUrl}">NHP</a>
             	<div class="nav-collapse collapse navbar-inverse-collapse">
                   <ul class="nav">
                 		<li><a href="${baseUrl}dashboard"><i class="icon-home icon-white"></i> Dashboard</a></li>
                     	<#list mainMenu as menuItem>
                     	<#if menuItem.getChilds()??>
                     		<li class="dropdown">
	                     		<a href="#" class="dropdown-toggle" data-toggle="dropdown">${menuItem.getName()} <b class="caret"></b></a>
	                     		<ul class="dropdown-menu">
	                     			<#list menuItem.getChilds() as subMenu>
	                     				<li><a href="${baseUrl}${subMenu.getUrl()}">${subMenu.getName()}</a></li>
	                     			</#list>
	                     		</ul>
	                     	</li>
                     	<#else>
                     		<li><a href="${baseUrl}${menuItem.getUrl()}">${menuItem.getName()}</a></li>
                     	</#if>
                     	</#list>
                   </ul>
                   <ul class="nav pull-right">
                   	<li class="divider-vertical"></li>
                   	<li class="dropdown">
                   		<a href="#" class="dropdown-toggle" data-toggle="dropdown"> Welcome <#if USER??>${USER.getDisplayName()!""}</#if> <b class="caret"></b></a>
                   		<ul class="dropdown-menu">
                   			<li class="nav-header"><i class="icon-user icon-white"></i> Manage Profile</li>
                         		<li><a href="${baseUrl}profile">Profile</a></li>
                          	<li><a href="${baseUrl}profile/changePassword">Change password</a></li>
                          	<li><a href="${baseUrl}profile/history">History</a></li>
                          	<li class="divider"></li>
                          	<li><a href="${baseUrl}logout"><i class="icon-off icon-white"></i> Logout</a></li>
                       	</ul>
                   	</li>
                   </ul>
                   <form class="navbar-search form-search pull-right" action="" style="margin-right: 30px">
                 		<div class="input-append">
						<input type="text" class="span2" placeholder="Search">
					    <button type="submit" class="btn">Search</button>
					</div>
                   </form>
             	</div><!-- /.nav-collapse -->
       	</div>
 		</div>
</div>