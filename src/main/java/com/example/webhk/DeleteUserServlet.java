package com.example.webhk;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/DeleteUserServlet")
public class DeleteUserServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取管理员提交的删除用户
        String[] selectedUsers = request.getParameterValues("selectedUsers");

        // 数据库连接信息（修改为MySQL）
        String dbUrl = "jdbc:mysql://localhost:3306/web?useSSL=false&serverTimezone=UTC"; // 修改为MySQL连接
        String dbUser = "root"; // 修改为MySQL的用户名
        String dbPassword = "123456"; // 修改为MySQL的密码

        if (selectedUsers != null) {
            try {
                // 加载MySQL数据库驱动
                Class.forName("com.mysql.cj.jdbc.Driver"); // 使用MySQL的驱动
                Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

                // 删除用户的SQL语句
                String deleteSql = "DELETE FROM Users WHERE username = ?";

                // 循环删除选中的用户
                PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                for (String username : selectedUsers) {
                    deleteStmt.setString(1, username);
                    deleteStmt.executeUpdate();
                }
                deleteStmt.close();
                conn.close();

                // 重定向回用户管理页面
                response.sendRedirect("ManageUsers.jsp");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "删除用户失败！");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "没有选择要删除的用户！");
        }
    }
}
