
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:html lang="ja">
<body>

<h1>html:form タグのサンプル</h1>
<html:form action="form-result">
  <html:submit/>
</html:form>

<html:form action="/form-result">
  <html:submit/>
</html:form>

<html:form action="form-result" style="border: solid 5px;">
  <html:submit/>
</html:form>

<html:form action="form-result" styleId="form_id">
  <html:submit/>
</html:form>

</body>
</html:html>