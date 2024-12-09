<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>购物商城首页</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
        }
        .header {
            display: flex;
            justify-content: space-between;
            padding: 10px 20px;
            background-color: #f4f4f4;
            border-bottom: 1px solid #ddd;
        }
        .header h1 {
            margin: 0;
        }
        .products {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
            padding: 20px;
            justify-content: center;
        }
        .product {
            width: 180px;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 10px;
            text-align: center;
            box-shadow: 0 0 5px rgba(0,0,0,0.1);
        }
        .product img {
            width: 100%;
            height: auto;
            border-radius: 5px;
        }
        .product-info {
            font-size: 14px;
            margin-top: 10px;
        }
        .product-info span {
            display: block;
            margin-bottom: 5px;
        }
    </style>
</head>
<body>
<div class="header">
    <h1>购物商城</h1>
    <div class="welcome">
        <%
            HttpSession session2 = request.getSession(false);
            if (session2 != null && session2.getAttribute("username") != null) {
                String username = (String) session2.getAttribute("username");
                // 查询数据库检查用户角色
                String dbUrl = "jdbc:mysql://localhost:3306/web?useSSL=false&serverTimezone=UTC"; // 修改为MySQL连接
                String dbUser = "root"; // MySQL用户
                String dbPassword = "123456"; // MySQL密码
                boolean isAdmin = false;

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL的JDBC驱动
                    Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                    String roleSql = "SELECT role FROM Users WHERE username = ?";
                    PreparedStatement roleStmt = conn.prepareStatement(roleSql);
                    roleStmt.setString(1, username);
                    ResultSet rs = roleStmt.executeQuery();
                    if (rs.next()) {
                        String role = rs.getString("role");
                        if ("order".equals(role)) {
                            isAdmin = true; // 用户为管理员
                        }
                    }
                    rs.close();
                    roleStmt.close();
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

        %>
        欢迎，<%= username %>！
        <a href="LogoutServlet">注销</a>
        <a href="Cart.jsp">我的购物车</a>
        <% if(isAdmin) {%>
        <a href="AddProduct.jsp">添加商品</a> <!-- 显示添加商品按钮 -->
        <a href="historyOrders.jsp">查看所有订单</a>
        <a href="financeReport.jsp">财务报表</a>
        <a href="ManageUsers.jsp">用户管理</a>  <!-- 添加管理用户功能的链接 -->
        <% }else{ %>
        <a href="historyOrders.jsp">查看历史订单</a>
        <% }} else { %>
        <a href="Login.jsp">登录</a>
        <a href="Register.jsp">注册</a>
        <% } %>
    </div>
</div>

<h1 style="text-align: center;">欢迎来到我们的购物商城！</h1>

<div class="products">
    <%
        String dbUrl = "jdbc:mysql://localhost:3306/web?useSSL=false&serverTimezone=UTC"; // 修改为MySQL连接
        String dbUser = "root"; // MySQL用户
        String dbPassword = "123456"; // MySQL密码

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL的JDBC驱动
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            String sql = "SELECT productID, ProductName, ProductImage, UnitPrice, Quantity, ProductType FROM Products";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int productID = rs.getInt("productID");
                String productName = rs.getString("ProductName");
                byte[] imageBytes = rs.getBytes("ProductImage"); // 图片二进制数据
                String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
                double unitPrice = rs.getDouble("UnitPrice");
                int quantity = rs.getInt("Quantity");
                String productType = rs.getString("ProductType");
    %>
    <div class="product">
        <a href="ProductDetail.jsp?productID=<%= productID %>"> <!-- 商品详情页链接 -->
            <img src="data:image/jpeg;base64,<%= base64Image %>" alt="<%= productName %>">
            <div class="product-info">
                <span><strong>名称：</strong><%= productName %></span>
                <span><strong>价格：</strong>¥<%= unitPrice %></span>
                <span><strong>库存：</strong><%= quantity %></span>
            </div>
        </a>
    </div>

    <%
        }
        rs.close();
        stmt.close();
        conn.close();
    } catch (Exception e) {
        e.printStackTrace();
    %>
    <p>加载商品失败，请稍后重试。</p>
    <% } %>
</div>
</body>
</html>
