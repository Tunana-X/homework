package com.example.webhk;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    // MySQL 数据库连接信息
    private static final String DB_URL = "jdbc:mysql://localhost:3306/web?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";  // MySQL 用户名
    private static final String DB_PASSWORD = "123456";  // MySQL 密码

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        try {
            // 加载 MySQL 驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 建立连接
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("连接数据库成功");

            // 检查用户名是否已存在
            String checkUserSql = "SELECT * FROM Users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkUserSql);
            checkStmt.setString(1, username);
            ResultSet checkRs = checkStmt.executeQuery();

            if (checkRs.next()) {
                // 用户名已存在
                request.setAttribute("errorMessage", "用户名已存在，请选择其他用户名！");
                request.getRequestDispatcher("Register.jsp").forward(request, response);
                return;
            }

            // 检查电子邮箱是否已存在
            String checkEmailSql = "SELECT * FROM Users WHERE email = ?";
            PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmailSql);
            checkEmailStmt.setString(1, email);
            ResultSet checkEmailRs = checkEmailStmt.executeQuery();

            if (checkEmailRs.next()) {
                // 电子邮箱已存在
                request.setAttribute("errorMessage", "电子邮箱已被注册！");
                request.setAttribute("username", username);
                request.setAttribute("password", password);
                request.getRequestDispatcher("Register.jsp").forward(request, response);
                return;
            }

            // 插入新用户
            String insertSql = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, email);

            int rows = insertStmt.executeUpdate();
            if (rows > 0) {
                response.sendRedirect("Login.jsp?success=Registered");
            } else {
                request.setAttribute("errorMessage", "注册失败，请稍后再试！");
                request.getRequestDispatcher("Register.jsp").forward(request, response);
            }

            // 关闭连接和资源
            insertStmt.close();
            checkEmailStmt.close();
            checkStmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("异常发生，原因：" + e.getMessage());
            e.printStackTrace(); // 打印完整堆栈信息
            response.sendRedirect("Register.jsp?error=server");
        }
    }
}
