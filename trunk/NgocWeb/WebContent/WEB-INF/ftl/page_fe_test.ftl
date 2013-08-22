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
    <form method="post" action="${baseUrl}/test" enctype="multipart/form-data">
        <input type="text" name="name"/>
        <input type="file" name="file"/>
        <input type="submit"/>
    </form>

</body>
</html>