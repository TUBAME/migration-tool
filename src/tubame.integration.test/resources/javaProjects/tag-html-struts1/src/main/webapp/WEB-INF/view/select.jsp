
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html:html lang="ja">
<body>

<h1>html:select タグのサンプル</h1>
<html:form action="select-result1">
  <html:select property="option">
    <html:option value="option1">Option1</html:option>
    <html:option value="option2">Option2</html:option>
    <html:option value="option3">Option3</html:option>
  </html:select>
  <html:submit/>
</html:form>

<html:form action="select-result2">
  <html:select property="option">
    <c:forEach items="${options}" var="option">
      <html:option value="${option.value}">
        ${option.label}
      </html:option>
    </c:forEach>
  </html:select>
  <html:submit/>
</html:form>

</body>
</html:html>