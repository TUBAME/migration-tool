<%@page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<html:html lang="ja">
<body>
<h1>html:hidden indexed タグのサンプル</h1>

<ul>
  <li>products[0].id: <bean:write name="HiddenIndexedForm" property="products[0].id"/></li>
  <li>products[1].id: <bean:write name="HiddenIndexedForm" property="products[1].id"/></li>
  <li>products[2].id: <bean:write name="HiddenIndexedForm" property="products[2].id"/></li>
</ul>

</body>
</html:html>