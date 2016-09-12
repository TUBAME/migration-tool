
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:html>
<body>

<h1>html:multibox タグのサンプル</h1>
<html:form action="/multibox-result">
    <ul>
        <li>checked1: <html:multibox property="checked1" disabled="true" value="checked1"/></li>
        <li>checked2: <html:multibox property="otherMultiboxProperty" name="otherMultiboxBean" value="checked2"/></li>
        <li>checked3: <html:multibox property="checked3" onclick="invoke_alert()" value="checked3"/></li>
        <li>checked4: <html:multibox property="checked4" styleId="checked4" value="checked4"/></li>
        <li>checked5: <html:multibox property="checked5" value="checked5"/></li>
        <li>checked6-1: <html:multibox property="checked6" value="checked6-1"/></li>
        <li>checked6-2: <html:multibox property="checked6" value="checked6-2"/></li>
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