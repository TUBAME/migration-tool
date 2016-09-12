
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<html:html>
<body>
<h1>html:text indexed タグのサンプル</h1>

<html:form action="/text-indexed-result">
    <logic:iterate id="product" name="TextIndexedForm" property="products">
        <div>
            <c:out value="${product.name}"/>
            <html:text name="product" property="amount" indexed="true"/>
        </div>
    </logic:iterate>
    <html:submit/>
</html:form>

</body>
</html:html>