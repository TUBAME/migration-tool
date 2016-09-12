
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:html locale="true">
<body>
<h1>html:html locale="true" タグのサンプル</h1>
<%= session.getAttribute("org.apache.struts.action.LOCALE") %>
</body>
</html:html>