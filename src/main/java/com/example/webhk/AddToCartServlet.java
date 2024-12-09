package com.example.webhk;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.*;

@WebServlet("/AddToCartServlet")
public class AddToCartServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = (String) request.getSession().getAttribute("username"); // 当前用户
        String productId = request.getParameter("productId");
        String productName = request.getParameter("productName");
        double unitPrice = Double.parseDouble(request.getParameter("unitPrice"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        double totalPrice = unitPrice * quantity;

        // MySQL数据库连接信息
        String dbUrl = "jdbc:mysql://localhost:3306/web?serverTimezone=UTC"; // 修改为你的数据库名称
        String dbUser = "root"; // 修改为你的数据库用户名
        String dbPassword = "123456"; // 修改为你的数据库密码

        try {
            // 加载MySQL驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // 检查用户是否已有购物车
            String cartCheckSql = "SELECT CartID FROM ShoppingCart WHERE Username = ?";
            PreparedStatement cartCheckStmt = conn.prepareStatement(cartCheckSql);
            cartCheckStmt.setString(1, username);
            ResultSet cartRs = cartCheckStmt.executeQuery();

            int cartId = 0;
            if (!cartRs.next()) {
                // 如果没有购物车，先为用户创建一个新的购物车
                String createCartSql = "INSERT INTO ShoppingCart (Username) VALUES (?)";
                PreparedStatement createCartStmt = conn.prepareStatement(createCartSql, Statement.RETURN_GENERATED_KEYS);
                createCartStmt.setString(1, username);
                createCartStmt.executeUpdate();

                // 获取新创建的购物车ID
                ResultSet generatedKeys = createCartStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    cartId = generatedKeys.getInt(1);
                }
                createCartStmt.close();
            } else {
                // 如果已有购物车，直接获取购物车ID
                cartId = cartRs.getInt("CartID");
            }
            cartRs.close();
            cartCheckStmt.close();

            // 检查用户是否已有此商品
            String checkSql = "SELECT Quantity FROM CartItems CI JOIN ShoppingCart SC ON CI.CartID = SC.CartID WHERE SC.Username = ? AND CI.ProductName = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            checkStmt.setString(2, productName);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // 更新已有商品的数量
                int existingQuantity = rs.getInt("Quantity");
                String updateSql = "UPDATE CartItems SET Quantity = ? WHERE CartID = ? AND ProductName = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, existingQuantity + quantity);
                updateStmt.setInt(2, cartId);
                updateStmt.setString(3, productName);
                updateStmt.executeUpdate();
                updateStmt.close();
            } else {
                // 插入新商品到购物车
                String insertSql = "INSERT INTO CartItems (CartID, ProductName, UnitPrice, Quantity) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, cartId);
                insertStmt.setString(2, productName);
                insertStmt.setDouble(3, unitPrice);
                insertStmt.setInt(4, quantity);
                insertStmt.executeUpdate();
                insertStmt.close();
            }

            // 设置响应编码为 UTF-8
            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");

            // 重定向回商品详情页并附带成功信息
            // 使用 URLEncoder 对中文参数进行编码
            String successMessage = URLEncoder.encode("添加到购物车成功", "UTF-8");
            response.sendRedirect("ProductDetail.jsp?productID=" + productId + "&success=" + successMessage);

            rs.close();
            checkStmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            // 设置响应编码为 UTF-8
            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            String warningMessage = URLEncoder.encode("您还没有登录，无法添加购物车","UTF-8");
            response.sendRedirect("ProductDetail.jsp?productID=" + productId + "&warning=" + warningMessage);
        }
    }
}
