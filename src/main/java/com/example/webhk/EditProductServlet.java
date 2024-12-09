package com.example.webhk;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@WebServlet("/EditProductServlet")
public class EditProductServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productId = request.getParameter("productId");
        double newPrice = Double.parseDouble(request.getParameter("newPrice"));
        int newQuantity = Integer.parseInt(request.getParameter("newQuantity"));

        // 修改为 MySQL 数据库连接信息
        String dbUrl = "jdbc:mysql://localhost:3306/web?useSSL=false&serverTimezone=UTC";
        String dbUser = "root"; // 修改为MySQL的用户名
        String dbPassword = "123456"; // 修改为MySQL的密码

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            String sql = "UPDATE Products SET UnitPrice = ?, Quantity = ? WHERE ProductID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDouble(1, newPrice);
                stmt.setInt(2, newQuantity);
                stmt.setInt(3, Integer.parseInt(productId));

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    response.sendRedirect("ProductDetail.jsp?productID=" + productId + "&success=" + URLEncoder.encode("商品信息修改成功", "UTF-8"));
                } else {
                    response.sendRedirect("ProductDetail.jsp?productID=" + productId + "&warning=" + URLEncoder.encode("商品信息修改失败", "UTF-8"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ProductDetail.jsp?productID=" + productId + "&warning=" + URLEncoder.encode("数据库连接失败", "UTF-8"));
        }
    }
}
