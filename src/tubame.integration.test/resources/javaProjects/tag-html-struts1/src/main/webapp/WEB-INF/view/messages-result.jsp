<%@page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<html:html lang="ja">
<body>
<h1>html:messages タグのサンプル</h1>

<h2>メッセージ</h2>

<h3>property あり</h3>
<div>
<html:messages id="msg" message="true" property="ANOTHER_MESSAGE">
    <bean:write name="msg"/>
</html:messages>

</div>

<h3>property なし</h3>
<div>
  <html:messages id="msg" message="true">
    <bean:write name="msg"/>
  </html:messages>
</div>

<h2>エラーメッセージ</h2>
<div>
<html:messages id="msg" message="false">
    <bean:write name="msg"/>
</html:messages>
</div>

</body>
</html:html>