<%@page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<html:html lang="ja">
<body>
<h1>html:messages タグのサンプル</h1>

<html:form action="/messages-result">
    <html:submit property="submit" value="送信"/>
</html:form>

</body>
</html:html>