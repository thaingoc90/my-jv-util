<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<body>
	<h1>Spring MVC Hello World Annotation Example</h1>
 
 	Language : <a href="?lang=en">English</a>|<a href="?lang=vn">VietNam</a>
 	
 	<h3>
		welcome.springmvc : <spring:message code="welcome.springmvc" text="default text" />
	</h3>
	<!-- 
	Current Locale : ${pageContext.response.locale}
	 -->
</body>
</html>