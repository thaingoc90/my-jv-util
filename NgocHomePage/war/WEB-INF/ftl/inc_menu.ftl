<div class="navbar navbar-inverse navbar-fixed-top">
	<div class="navbar-inner">
           <div class="container">
             	<a class="brand" href="#">NHP</a>
             	<div class="nav-collapse collapse navbar-inverse-collapse">
                   <ul class="nav">
                 		<li class="active"><a href="#"><i class="icon-home icon-white"></i> Dashboard</a></li>
                 		<li class="dropdown">
                       	<a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="icon-group icon-white"></i> Manage <b class="caret"></b></a>
                        <ul class="dropdown-menu">
                          	<li><a href="${baseUrl}groups">Groups</a></li>
                          	<li><a href="${baseUrl}users">Users</a></li>
                          	<li><a href="#">Permissions</a></li>
                        </ul>
                     	</li>
                     	<li class="dropdown">
                       	<a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="icon-gear icon-white"></i> Setting <b class="caret"></b></a>
                        <ul class="dropdown-menu">
                          	<li><a href="#">Account</a></li>
                          	<li><a href="#">Primacy</a></li>
                        </ul>
                     	</li>
                   </ul>
                   <ul class="nav pull-right">
                   	<li class="divider-vertical"></li>
                   	<li class="dropdown">
                   		<a href="#" class="dropdown-toggle" data-toggle="dropdown"> Welcome admin <b class="caret"></b></a>
                   		<ul class="dropdown-menu">
                   			<li class="nav-header"><i class="icon-user icon-white"></i> Manage Profile</li>
                         		<li><a href="#">Profile</a></li>
                          	<li><a href="#">Change password</a></li>
                          	<li><a href="#">History</a></li>
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