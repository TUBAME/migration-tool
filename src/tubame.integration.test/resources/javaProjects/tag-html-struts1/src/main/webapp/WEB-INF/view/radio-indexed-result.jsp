
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:html>
<body>
<h1>html:radio indexed タグのサンプル</h1>
<ul>
  <li>product[0].like: <%= request.getParameter("product[0].like") %></li>
  <li>product[1].like: <%= request.getParameter("product[1].like") %></li>
  <li>product[2].like: <%= request.getParameter("product[2].like") %></li>
</ul>
</body>
</html:html>