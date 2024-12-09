package com.example.webhk;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    // MySQL 数据库连接信息
    private static final String DB_URL = "jdbc:mysql://localhost:3306/web?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";  // MySQL 用户名
    private static final String DB_PASSWORD = "123456";  // MySQL 密码

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            // 加载 MySQL 驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 建立连接
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // 检测用户名是否存在
            String checkUsernameSql = "SELECT * FROM Users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkUsernameSql);
            checkStmt.setString(1, username);

            ResultSet checkRs = checkStmt.executeQuery();
            if (!checkRs.next()) {
                // 用户名不存在
                request.setAttribute("error", "未找到用户名");
                request.setAttribute("username", ""); // 清空用户名
                request.getRequestDispatcher("Login.jsp").forward(request, response);
            } else {
                // 检测密码是否正确
                String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    // 登录成功
                    HttpSession session = request.getSession();
                    session.setAttribute("username", username);
                    request.setAttribute("message", "登陆成功!欢迎 " + username + "!");
                    request.setAttribute("redirectUrl", "index.jsp");
                    request.getRequestDispatcher("success.jsp").forward(request, response);
                } else {
                    // 密码错误
                    request.setAttribute("error", "密码错误");
                    request.setAttribute("username", username); // 保留用户名
                    request.getRequestDispatcher("Login.jsp").forward(request, response);
                }

                rs.close();
                stmt.close();
            }

            // 关闭资源
            checkRs.close();
            checkStmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            // 记录异常详细信息
            e.printStackTrace();  // 打印堆栈信息到控制台
            request.setAttribute("error", "服务器错误: " + e.getMessage());  // 将错误信息传递到前端
            request.getRequestDispatcher("Login.jsp").forward(request, response);
        }
    }

}
