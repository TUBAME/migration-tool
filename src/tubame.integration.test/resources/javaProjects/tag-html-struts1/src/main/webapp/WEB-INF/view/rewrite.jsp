
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:html lang="ja">
<body>
<h1>html:rewrite タグのサンプル</h1>
<script src="<html:rewrite page='/assets/alert.js'/>"></script>

<ul>
  <li><input type="button" onclick="invoke_alert()" value="Invoke JavaScript"/></li>
  <li><a href="<html:rewrite action='/rewrite-another-action'/>">Go to another action!</a></li>
</ul>

</body>
</html:html>