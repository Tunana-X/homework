package com.example.webhk;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/FinanceReportServlet")
public class FinanceReportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // MySQL数据库连接信息
    private static final String DB_URL = "jdbc:mysql://localhost:3306/web?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root"; // 修改为MySQL的用户名
    private static final String DB_PASSWORD = "123456"; // 修改为MySQL的密码

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONArray dateData = new JSONArray();  // 按日期统计
        JSONArray typeData = new JSONArray();  // 按商品样式统计

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // 按日期统计总销售额
            String dateSql = "SELECT DATE_FORMAT(OrderDate, '%Y-%m-%d') AS OrderDate, SUM(TotalAmount) AS TotalSales " +
                    "FROM Orders GROUP BY DATE_FORMAT(OrderDate, '%Y-%m-%d') ORDER BY OrderDate";
            PreparedStatement dateStmt = conn.prepareStatement(dateSql);
            ResultSet dateRs = dateStmt.executeQuery();
            while (dateRs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("date", dateRs.getString("OrderDate"));
                obj.put("sales", dateRs.getDouble("TotalSales"));
                dateData.put(obj);
            }

            // 按商品名称统计总销售额
            String typeSql = "SELECT ProductName, SUM(Subtotal) AS TotalSales " +
                    "FROM OrderItems GROUP BY ProductName ORDER BY TotalSales DESC";
            PreparedStatement typeStmt = conn.prepareStatement(typeSql);
            ResultSet typeRs = typeStmt.executeQuery();
            while (typeRs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("type", typeRs.getString("ProductName"));
                obj.put("sales", typeRs.getDouble("TotalSales"));
                typeData.put(obj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject result = new JSONObject();
        result.put("dateData", dateData);
        result.put("typeData", typeData);

        // 输出结果
        PrintWriter out = response.getWriter();
        out.print(result);
        out.flush();
    }
}
