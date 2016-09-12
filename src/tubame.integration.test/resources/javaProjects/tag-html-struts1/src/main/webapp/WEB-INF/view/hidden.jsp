<%@page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:html lang="ja">
<body>
<h1>html:hidden タグのサンプル</h1>
<html:form action="/hidden-result">
    <html:hidden property="hidden1"/>
    <html:hidden property="hidden2" styleId="hidden2-id" value="hidden2-value"/>
    <html:hidden property="otherHiddenProperty" name="otherHiddenBean"/>
    <html:submit/>
</html:form>
</body>
</html:html>