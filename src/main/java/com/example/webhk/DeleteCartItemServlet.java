package com.example.webhk;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import org.json.JSONObject;

@WebServlet("/DeleteCartItemServlet")
public class DeleteCartItemServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;

        // 读取 JSON 数据
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        try {
            // 解析 JSON 数据
            JSONObject json = new JSONObject(sb.toString());
            int cartItemId = json.getInt("cartItemId");

            String dbUrl = "jdbc:mysql://localhost:3306/web?useSSL=false&serverTimezone=UTC"; // 修改为MySQL数据库连接
            String dbUser = "root";  // 修改为你MySQL的用户名
            String dbPassword = "123456"; // 修改为你MySQL的密码

            // 加载MySQL JDBC驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // 删除购物车条目
            String sql = "DELETE FROM CartItems WHERE CartItemID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cartItemId);
            int rows = stmt.executeUpdate();

            stmt.close();
            conn.close();

            if (rows > 0) {
                response.getWriter().write("Success");
            } else {
                response.getWriter().write("Failed to delete cart item");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
}
