
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:html>
<body>
<h1>html:text indexed タグのサンプル</h1>
<ul>
  <li>product[0].amount: <%= request.getParameter("product[0].amount") %></li>
  <li>product[1].amount: <%= request.getParameter("product[1].amount") %></li>
  <li>product[2].amount: <%= request.getParameter("product[2].amount") %></li>
</ul>
</body>
</html:html>