<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <title>用户注册</title>
  <style>
    .body-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100vh;
      margin: 0;
    }
    .register-content {
      width: 300px;
      padding: 20px;
      display: flex;
      flex-direction: column;
      align-items: center;
      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    }
    .error-message {
      color: red;
      margin-top: 10px;
      font-size: 14px;
    }
    .login-link {
      margin-top: 20px;
    }
  </style>
</head>
<body>
<a href="index.jsp" style="position: absolute; top: 10px; left: 10px;">
  返回首页
</a>
<div class="body-content">
  <h1>用户注册</h1>
  <form class="register-content" action="RegisterServlet" method="post">
    <input type="text" name="username" value="${username != null? username : ''}" placeholder="请输入用户名" required><br>
    <input type="password" name="password" value="${password!=null?password:''}" placeholder="请输入密码" required><br>
    <input type="email" name="email" placeholder="请输入电子邮箱" title="请输入有效的邮箱号"><br>
    <button type="submit">注册</button>
    <!-- 显示错误信息 -->
    <c:if test="${not empty errorMessage}">
      <div class="error-message">${errorMessage}</div>
    </c:if>
  </form>
  <div class="login-link">
    <a href="Login.jsp">已有账号？登录</a>
  </div>
</div>
</body>
</html>
