package com.example.webhk;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@WebServlet("/AddProductServlet")
@MultipartConfig
public class AddProductServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/web?serverTimezone=UTC"; // MySQL 的连接 URL
    private static final String DB_USER = "root"; // MySQL 用户名
    private static final String DB_PASSWORD = "123456"; // MySQL 密码

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productName = request.getParameter("productName");
        double unitPrice = Double.parseDouble(request.getParameter("unitPrice"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String productType = request.getParameter("productType");

        // 获取图片
        Part filePart = request.getPart("productImage");
        InputStream inputStream = filePart.getInputStream();

        try {
            // 加载 MySQL 驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "INSERT INTO Products (ProductName, ProductImage, UnitPrice, Quantity, ProductType, ProductHot) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, productName);
            stmt.setBlob(2, inputStream);  // 使用 setBlob 来处理二进制文件
            stmt.setDouble(3, unitPrice);
            stmt.setInt(4, quantity);
            stmt.setString(5, productType);
            stmt.setInt(6, 1); // 默认设置 ProductHot 为 1

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                response.sendRedirect("index.jsp"); // 添加商品后重定向到主页面
            } else {
                response.sendRedirect("AddProduct.jsp?error=failed");
            }

            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("AddProduct.jsp?error=server");
        }
    }
}
