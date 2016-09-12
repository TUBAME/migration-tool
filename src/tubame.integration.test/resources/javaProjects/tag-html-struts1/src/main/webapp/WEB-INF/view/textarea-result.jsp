<%@page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<html:html lang="ja">
<body>

<h1>html:textarea タグのサンプル</h1>
<ul>
    <li>[property] <bean:write name="TextAreaForm" property="textarea1"/></li>
    <li>[disabled] <bean:write name="TextAreaForm"  property="textarea2"/></li>
    <li>[style] <bean:write name="TextAreaForm" property="textarea3"/></li>
    <li>[value] <bean:write name="TextAreaForm"  property="textarea4"/></li>
    <li>[cols] <bean:write name="TextAreaForm"  property="textarea5"/></li>
    <li>[rows] <bean:write name="TextAreaForm"  property="textarea6"/></li>
</ul>
<html:submit/>

</body>
</html:html>