<%@page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<html:html lang="ja">
<body>

<h1>html:text タグのサンプル</h1>
<ul>
  <li>[property] <bean:write name="TextForm" property="text1"/></li>
  <li>[maxlength] <bean:write name="TextForm" property="text2"/></li>
  <li>[name] <bean:write name="otherTextBean" property="otherTextProperty"/></li>
  <li>[readonly] <bean:write name="TextForm" property="text3"/></li>
  <li>[size] <bean:write name="TextForm" property="text4"/></li>
  <li>[style] <bean:write name="TextForm" property="text5"/></li>
  <li>[styleClass] <bean:write name="TextForm" property="text6"/></li>
  <li>[styleId] <bean:write name="TextForm" property="text7"/></li>
</ul>

</body>
</html:html>