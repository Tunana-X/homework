package com.example.webhk;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.io.BufferedReader;
import org.json.JSONObject;

@WebServlet("/UpdateCartItemServlet") // 确保 URL 映射正确
public class UpdateCartItemServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        try {
            // 解析 JSON 数据
            JSONObject json = new JSONObject(sb.toString());
            int cartItemId = json.getInt("cartItemId");
            int quantity = json.getInt("quantity");

            String dbUrl = "jdbc:mysql://localhost:3306/web?useSSL=false&serverTimezone=UTC";
            String dbUser = "root";
            String dbPassword = "123456";

            // 加载 MySQL 驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            String sql = "UPDATE CartItems SET Quantity = ? WHERE CartItemID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quantity);
            stmt.setInt(2, cartItemId);
            int rows = stmt.executeUpdate();

            stmt.close();
            conn.close();

            if (rows > 0) {
                response.getWriter().write("Success");
            } else {
                response.getWriter().write("Failed to update cart item");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
}
