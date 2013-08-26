<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8" />
    <meta name="description" content="description" />
    <meta name="keywords" content="contents" />
    <meta name="author" content="SPINNING" />
    
</head>
<body>

	<h1>Please upload a file</h1>
	
	<#include "inc_error_zone.ftl" />   
    <form method="post" action="${baseUrl}/test" enctype="multipart/form-data">
        <input type="text" name="name"/>
        <input type="file" name="file" required data-maxsize="10000000" data-extensions=".mp3, .rar, .zip"/>
        <input type="submit"/>
    </form>
    

    <script type="text/javascript" src="${staticResourceRoot}js/jquery-2.0.2.min.js" charset="utf-8"></script>
    <script type="text/javascript" src="${staticResourceRoot}js/check_upload_file.js" charset="utf-8"></script>    
</body>
</html>