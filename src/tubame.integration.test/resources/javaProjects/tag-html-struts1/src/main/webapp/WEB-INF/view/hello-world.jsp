<%@page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<html:html lang="ja">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Hello World Struts1</title>
</head>
<body>
<h1>
    <bean:message key="welcome" /> <bean:write name="HelloWorldForm" property="name" /> さん
</h1>
</body>
</html:html>