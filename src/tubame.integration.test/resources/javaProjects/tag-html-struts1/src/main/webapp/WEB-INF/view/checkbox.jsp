
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:html>
<body>

<h1>html:checkbox タグのサンプル</h1>
<html:form action="/checkbox-result">
    <ul>
        <li>checked1: <html:checkbox property="checked1" styleId="checked1"/></li>
        <li>checked2: <html:checkbox property="checked2" value="checked2"/></li>
        <li>checked3: <html:checkbox property="checked3" onclick="invoke_alert()"/></li>
        <li>checked4: <html:checkbox property="checked4" style="vertical-align: middle;"/></li>
        <li>checked5: <html:checkbox property="checked5" disabled="true"/></li>
    </ul>
    <html:submit/>
</html:form>

<script>
  var invoke_alert = function() {
    alert('pressed button!');
  }
</script>
</body>
</html:html>