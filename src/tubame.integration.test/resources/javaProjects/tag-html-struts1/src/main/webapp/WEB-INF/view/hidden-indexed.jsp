<%@page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<html:html lang="ja">
<body>
<h1>html:hidden indexed タグのサンプル</h1>
<html:form action="/hidden-indexed-result">
  <table>
    <tr>
      <th>Product ID</th>
      <th>Name</th>
    </tr>
    <logic:iterate id="product" name="HiddenIndexedForm" property="products">
      <html:hidden name="product" property="id" indexed="true"/>
      <tr>
        <td><bean:write name="product" property="id"/></td>
        <td><bean:write name="product" property="name"/></td>
      </tr>
    </logic:iterate>
  </table>
  <html:submit value="Get hidden values "/>
</html:form>
</body>
</html:html>