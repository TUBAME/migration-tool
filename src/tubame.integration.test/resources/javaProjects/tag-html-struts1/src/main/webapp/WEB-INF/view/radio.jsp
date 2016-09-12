
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:html>
<body>
<h1>html:radio タグのサンプル</h1>
<html:form action="/radio-result">
    <html:radio property="radio1" value="check1">ラジオ1</html:radio>
    <html:radio property="radio1" value="check2" disabled="true">ラジオ2(無効化)</html:radio>
    <html:radio property="radio1" value="check3" onclick="invoke_alert()">ラジオ3(onclick)</html:radio>
    <html:radio property="otherRadioProperty" name="otherRadioBean" value="other-radio">他のRadioBean</html:radio>
    <html:submit/>
</html:form>

<script>
var invoke_alert = function() {
  alert('pressed button!');
}
</script>
</body>
</html:html>