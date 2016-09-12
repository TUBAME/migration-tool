<%@page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:html lang="ja">
<body>

<h1>html:textarea タグのサンプル</h1>
<html:form action="/textarea-result">
    <ul>
        <li>[property] 対応する ActionForm のプロパティ <html:textarea property="textarea1"/></li>
        <li>[disabled] 入力不可 <html:textarea property="textarea2" disabled="true"/></li>
        <li>[style] HTML の style 属性と同様 <html:textarea property="textarea3" style="background-color: red;"/></li>
        <li>[value] フィールド初期値 <html:textarea property="textarea4" value="initial-value"/></li>
        <li>[cols] 入力フィールドの横幅 <html:textarea property="textarea5" cols="100"/></li>
        <li>[rows] 入力フィールドの行数 <html:textarea property="textarea6" rows="10"/></li>
    </ul>
    <html:submit/>
</html:form>

</body>
</html:html>