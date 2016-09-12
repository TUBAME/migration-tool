<%@page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<html:html lang="ja">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Hello Struts1</title>
</head>
<body>
<html:form action="/HelloWorld">
    <bean:message key="greeting"/><br/>
    お名前をどうぞ。<br/>
    <html:text property="name"/><br/>
    <html:submit>
        <bean:message key="greeting"/>
    </html:submit>
</html:form>
</body>
</html:html>