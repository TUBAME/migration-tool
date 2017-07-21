<%@ page language="java" buffer="1kb" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
  HttpSession mysession  = request.getSession(true);
  out.println("session="+mysession);
  // out.flush();

  <!--

  mysession  = request.getSession(true);

  -->
  mysession.invalidate();

%>