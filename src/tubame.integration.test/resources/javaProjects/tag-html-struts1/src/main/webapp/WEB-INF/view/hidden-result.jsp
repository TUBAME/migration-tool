<%@page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<html:html lang="ja">
<body>
<h1>html:hidden タグのサンプル</h1>

<ul>
  <li>hidden1: <bean:write name="HiddenForm" property="hidden1"/></li>
  <li>hidden2: <bean:write name="HiddenForm" property="hidden2"/></li>
</ul>

</body>
</html:html>