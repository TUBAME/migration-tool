<%@page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:html lang="ja">
<body>

<h1>html:text タグのサンプル</h1>
<html:form action="/text-result">
    <ul>
        <li>[property] 対応する ActionForm のプロパティ <html:text property="text1"/></li>
        <li>[maxlength] 入力文字数を制限 <html:text property="text2" maxlength="5"/></li>
        <li>[name] 対応する ActionForm 以外の Bean <html:text property="otherTextProperty" name="otherTextBean"/></li>
        <li>[readonly] 読取専用 <html:text property="text3" readonly="true"/></li>
        <li>[size] 入力フィールドの横幅 <html:text property="text4" size="100"/></li>
        <li>[style] HTML の style 属性と同様 <html:text property="text5" style="background-color: red;"/></li>
        <li>[styleClass] HTML の class 属性と同様 <html:text property="text6" styleClass="text6-class"/></li>
        <li>[styleId] HTML の id 属性と同様 <html:text property="text7" styleId="text7_id"/></li>
    </ul>
    <html:submit/>
</html:form>

</body>
</html:html>