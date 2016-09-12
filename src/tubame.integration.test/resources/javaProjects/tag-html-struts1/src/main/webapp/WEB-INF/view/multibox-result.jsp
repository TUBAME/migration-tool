
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html:html>
<body>
<h1>html:multibox タグのサンプル</h1>
<ul>
    <li>checked1: <bean:write name="MultiboxForm" property="checked1"/></li>
    <li>checked2: <bean:write name="otherMultiboxBean" property="otherMultiboxProperty"/></li>
    <li>checked3: <bean:write name="MultiboxForm" property="checked3"/></li>
    <li>checked4: <bean:write name="MultiboxForm" property="checked4"/></li>
    <li>checked5: <bean:write name="MultiboxForm" property="checked5"/></li>
    <li>
      checked6:
      <logic:notEmpty name="MultiboxForm" property="checked6">
        <logic:iterate id="each" name="MultiboxForm" property="checked6" >
          <bean:write name="each"/>
        </logic:iterate>
      </logic:notEmpty>
    </li>
</ul>
</body>
</html:html>