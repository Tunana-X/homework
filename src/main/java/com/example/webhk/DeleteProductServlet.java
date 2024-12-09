package com.example.webhk;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@WebServlet("/DeleteProductServlet")
public class DeleteProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // 数据库连接信息
    private static final String DB_URL = "jdbc:mysql://localhost:3306/web?useSSL=false&serverTimezone=UTC"; // 修改为MySQL连接
    private static final String DB_USER = "root"; // 修改为MySQL的用户名
    private static final String DB_PASSWORD = "123456"; // 修改为MySQL的密码

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String productId = request.getParameter("productId"); // 获取传递的商品ID

        if (productId == null || productId.isEmpty()) {
            response.sendRedirect("index.jsp?warning=" + encodeMessage("商品ID无效！"));
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // 加载MySQL数据库驱动
            Class.forName("com.mysql.cj.jdbc.Driver"); // 修改为MySQL驱动
            // 建立数据库连接
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // 删除商品的SQL语句
            String sql = "DELETE FROM Products WHERE ProductID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(productId)); // 设置商品ID

            int rowsDeleted = stmt.executeUpdate(); // 执行删除操作

            if (rowsDeleted > 0) {
                // 删除成功，重定向到主页并显示成功信息
                response.sendRedirect("index.jsp?success=" + encodeMessage("商品删除成功！"));
            } else {
                // 删除失败，商品不存在
                response.sendRedirect("index.jsp?warning=" + encodeMessage("商品不存在或删除失败！"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常时，返回警告信息
            response.sendRedirect("index.jsp?warning=" + encodeMessage("删除过程中发生错误！"));
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 对消息进行编码以便在URL中传递
    private String encodeMessage(String message) {
        try {
            return java.net.URLEncoder.encode(message, "UTF-8");
        } catch (Exception e) {
            return message;
        }
    }
}
