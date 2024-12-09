package com.example.webhk;

import org.json.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;

@WebServlet("/CheckoutServlet")
public class CheckoutServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应类型
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        // 读取请求体中的 JSON 数据
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        // 解析 JSON 数据
        String jsonData = stringBuilder.toString();
        JSONObject jsonObject = new JSONObject(jsonData);

        // 获取用户名
        String username = jsonObject.getString("username");

        // 数据库连接信息
        String dbUrl = "jdbc:mysql://localhost:3306/web?useSSL=false&serverTimezone=UTC"; // 修改为MySQL数据库连接
        String dbUser = "root";  // 修改为你MySQL的用户名
        String dbPassword = "123456"; // 修改为你MySQL的密码

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            // 查询购物车中的所有商品
            String cartSql = "SELECT ci.CartItemID, ci.Quantity, ci.ProductName, p.Quantity AS StockQuantity, p.UnitPrice " +
                    "FROM CartItems ci JOIN Products p ON ci.ProductName = p.ProductName " +
                    "WHERE ci.CartID = (SELECT CartID FROM ShoppingCart WHERE Username = ?)";
            PreparedStatement cartStmt = conn.prepareStatement(cartSql);
            cartStmt.setString(1, username);
            ResultSet cartRs = cartStmt.executeQuery();

            // 计算订单总金额
            double totalAmount = 0;

            // 创建订单并保存到数据库
            String insertOrderSql = "INSERT INTO Orders (Username, TotalAmount) VALUES (?, ?)";
            PreparedStatement insertOrderStmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
            insertOrderStmt.setString(1, username);
            insertOrderStmt.setDouble(2, totalAmount);
            insertOrderStmt.executeUpdate();

            ResultSet generatedKeys = insertOrderStmt.getGeneratedKeys();
            int orderId = 0;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            }

            // 保存订单项
            while (cartRs.next()) {
                int cartItemId = cartRs.getInt("CartItemID");
                int cartQuantity = cartRs.getInt("Quantity");
                String productName = cartRs.getString("ProductName");
                int stockQuantity = cartRs.getInt("StockQuantity");
                double unitPrice = cartRs.getDouble("UnitPrice");

                // 计算小计
                double subtotal = cartQuantity * unitPrice;

                // 更新库存
                int newStockQuantity = stockQuantity - cartQuantity;
                if (newStockQuantity >= 0) {
                    String updateStockSql = "UPDATE Products SET Quantity = ? WHERE ProductName = ?";
                    PreparedStatement updateStockStmt = conn.prepareStatement(updateStockSql);
                    updateStockStmt.setInt(1, newStockQuantity);
                    updateStockStmt.setString(2, productName);
                    updateStockStmt.executeUpdate();
                    updateStockStmt.close();

                    // 保存订单项
                    String insertOrderItemSql = "INSERT INTO OrderItems (OrderID, ProductName, Quantity, UnitPrice, Subtotal) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement insertOrderItemStmt = conn.prepareStatement(insertOrderItemSql);
                    insertOrderItemStmt.setInt(1, orderId);
                    insertOrderItemStmt.setString(2, productName);
                    insertOrderItemStmt.setInt(3, cartQuantity);
                    insertOrderItemStmt.setDouble(4, unitPrice);
                    insertOrderItemStmt.setDouble(5, subtotal);
                    insertOrderItemStmt.executeUpdate();
                    insertOrderItemStmt.close();

                    // 从购物车中移除条目
                    String deleteCartSql = "DELETE FROM CartItems WHERE CartItemID = ?";
                    PreparedStatement deleteCartStmt = conn.prepareStatement(deleteCartSql);
                    deleteCartStmt.setInt(1, cartItemId);
                    deleteCartStmt.executeUpdate();
                    deleteCartStmt.close();

                    // 如果库存为 0，删除商品
                    if (newStockQuantity == 0) {
                        String deleteProductSql = "DELETE FROM Products WHERE ProductName = ?";
                        PreparedStatement deleteProductStmt = conn.prepareStatement(deleteProductSql);
                        deleteProductStmt.setString(1, productName);
                        deleteProductStmt.executeUpdate();
                        deleteProductStmt.close();
                    }

                    // 累加总金额
                    totalAmount += subtotal;
                }
            }

            // 更新订单总金额
            String updateOrderTotalSql = "UPDATE Orders SET TotalAmount = ? WHERE OrderID = ?";
            PreparedStatement updateOrderTotalStmt = conn.prepareStatement(updateOrderTotalSql);
            updateOrderTotalStmt.setDouble(1, totalAmount);
            updateOrderTotalStmt.setInt(2, orderId);
            updateOrderTotalStmt.executeUpdate();
            updateOrderTotalStmt.close();

            cartRs.close();
            cartStmt.close();
            conn.close();

            // 返回成功消息
            response.getWriter().write("Checkout successful");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
}
