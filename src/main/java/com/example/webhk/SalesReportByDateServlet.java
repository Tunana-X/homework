package com.example.webhk;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/SalesReportByDateServlet")
public class SalesReportByDateServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productId = request.getParameter("productID");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // 检查 productId 是否有效
        if (productId == null || !productId.matches("\\d+")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\":\"Invalid product ID.\"}");
            return;
        }

        String dbUrl = "jdbc:mysql://localhost:3306/web?useSSL=false&serverTimezone=UTC";
        String dbUser = "root";
        String dbPassword = "123456";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            // 使用 JOIN 查询获取产品名称和销售数据
            String sql = "SELECT P.ProductName, " +
                    "DATE_FORMAT(O.OrderDate, '%Y-%m-%d') AS date, " +
                    "SUM(oi.Quantity * oi.UnitPrice) AS sales " +
                    "FROM Orders O " +
                    "JOIN OrderItems oi ON O.OrderID = oi.OrderID " +
                    "JOIN Products P ON oi.ProductID = P.ProductID " +
                    "WHERE oi.ProductID = ? " +
                    "GROUP BY P.ProductName, DATE_FORMAT(O.OrderDate, '%Y-%m-%d') " +
                    "ORDER BY date ASC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(productId));  // 设置 productId 参数
            ResultSet rs = stmt.executeQuery();

            JSONArray salesData = new JSONArray();
            while (rs.next()) {
                JSONObject sale = new JSONObject();
                sale.put("date", rs.getString("date"));
                sale.put("sales", rs.getDouble("sales"));
                salesData.put(sale);
            }

            // 返回销售数据的 JSON 格式
            out.print(salesData.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\":\"Database query failed: " + e.getMessage() + "\"}");
        }
    }
}
