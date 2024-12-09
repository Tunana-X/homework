<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <title>提示信息</title>
  <meta http-equiv="refresh" content="3;URL=<%= request.getAttribute("redirectUrl") %>">
  <style>
    body {
      font-family: Arial, sans-serif;
      text-align: center;
      margin-top: 50px;
    }
  </style>
</head>
<body>
<h1><%= request.getAttribute("message") %></h1>
<p>页面将在3秒后跳转到主页面...</p>
</body>
</html>
