
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<html:html>
<body>
<h1>html:checkbox タグのサンプル</h1>
<ul>
    <li>checked1: <bean:write name="CheckboxForm" property="checked1"/></li>
    <li>checked2: <bean:write name="CheckboxForm" property="checked2"/></li>
    <li>checked3: <bean:write name="CheckboxForm" property="checked3"/></li>
    <li>checked3: <bean:write name="CheckboxForm" property="checked4"/></li>
    <li>checked3: <bean:write name="CheckboxForm" property="checked5"/></li>
</ul>
</body>
</html:html>