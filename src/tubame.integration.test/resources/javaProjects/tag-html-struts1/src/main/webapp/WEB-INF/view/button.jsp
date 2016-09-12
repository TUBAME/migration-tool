
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:html lang="ja">
<body>
<h1>html:button タグのサンプル</h1>

<html:button property="btn1" onclick="invoke_alert()"/>
<html:button property="btn2" disabled="true"/>
<html:button property="btn3" style="background-color: red;"/>
<html:button property="btn4" styleId="btn4"/>
<html:button property="btn5" value="btn5"/>

<hr/>

<html:form action="/button-result">
  <html:button property="buttonFromForm"/>
</html:form>

<script>
  var invoke_alert = function() {
    alert('pressed button!');
  }
</script>
</body>
</html:html>