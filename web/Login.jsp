<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <title>用户登录</title>
  <style>
    /* 使用Flexbox实现居中 */
    .body-content {
      display: flex;
      flex-direction: column; /* 使子元素垂直排列 */
      justify-content: center;
      align-items: center;
      height: 100vh; /* 使容器高度占满整个视口高度 */
      margin: 0;
    }
    .error-message {
      color: red;
      margin-top: 10px;
      font-size: 14px;
    }
    /* 表单样式 */
    .login-form {
      width: 300px; /* 根据需要调整宽度 */
      padding: 20px;
      display: flex;
      flex-direction: column;
      align-items: center;
      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); /* 可选，为表单添加阴影 */
    }
    /* 链接样式 */
    .register-link {
      margin-top: 20px; /* 在登录表单和注册链接之间添加一些间距 */
    }
  </style>
</head>
<body>
<a href="index.jsp" style="position: absolute; top: 10px; left: 10px;">
  返回首页
</a>
<div class="body-content">
  <h1>用户登录</h1>
  <form class="login-form" action="LoginServlet" method="post">
    <input type="text" name="username" value="${username != null ? username : ''}" placeholder="请输入用户名" required><br>
    <input type="password" name="password" placeholder="请输入密码" required><br>
    <button type="submit">登录</button>
    <!-- 仅当 error 不为空时显示错误信息 -->
    <c:if test="${not empty error}">
      <div class="error-message">${error}</div>
    </c:if>
  </form>
  <div class="register-link">
    <a href="Register.jsp">没有账号？注册</a>
  </div>
</div>
</body>
</html>