<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>用户管理</title>
    <style>
        /* 样式略 */
    </style>
</head>
<body>
<h1>用户管理</h1>
<%
    HttpSession session2 = request.getSession(false);
    if (session2 != null && session2.getAttribute("username") != null) {
        String username = (String) session2.getAttribute("username");

        // 判断用户角色是否为管理员
        String dbUrl = "jdbc:mysql://localhost:3306/web?useSSL=false&serverTimezone=UTC"; // 修改为MySQL连接
        String dbUser = "root"; // MySQL 用户
        String dbPassword = "123456"; // MySQL 密码
        boolean isAdmin = false;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // 使用 MySQL JDBC 驱动
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            String roleSql = "SELECT role FROM Users WHERE username = ?";
            PreparedStatement roleStmt = conn.prepareStatement(roleSql);
            roleStmt.setString(1, username);
            ResultSet rs = roleStmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                if ("order".equals(role)) {  // 假设管理员角色为 "order"
                    isAdmin = true;
                }
            }
            rs.close();
            roleStmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isAdmin) {
%>
<form action="DeleteUserServlet" method="post">
    <table border="1">
        <tr>
            <th>选择</th>
            <th>用户名</th>
            <th>邮箱</th>
            <th>操作</th>
        </tr>
        <%
            // 查询所有用户
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // 使用 MySQL JDBC 驱动
                Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                String sql = "SELECT username, email FROM Users";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    String dbUsername = rs.getString("username");
                    String email = rs.getString("email");
        %>
        <tr>
            <td><input type="checkbox" name="selectedUsers" value="<%= dbUsername %>"></td>
            <td><%= dbUsername %></td>
            <td><%= email %></td>
            <td><button type="submit" name="deleteUser" value="<%= dbUsername %>">删除</button></td>
        </tr>
        <%
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        %>
    </table>
</form>
<%
        } else {
            out.println("<p>您没有权限访问此页面。</p>");
        }
    } else {
        out.println("<p>请先登录。</p>");
    }
%>
</body>
</html>
