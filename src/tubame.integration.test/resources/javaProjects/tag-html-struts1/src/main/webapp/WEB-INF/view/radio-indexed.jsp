
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<html:html>
<body>
<h1>html:radio indexed タグのサンプル</h1>

<html:form action="/radio-indexed-result">
    <logic:iterate id="product" name="RadioIndexedForm" property="products">
        <div>
            <c:out value="${product.name}"/>
            <html:radio name="product" property="like" value="true" indexed="true">
                Like!
            </html:radio>
            <html:radio name="product" property="like" value="false" indexed="true">
                Does not like...
            </html:radio>
        </div>
    </logic:iterate>
    <html:submit/>
</html:form>

</body>
</html:html>