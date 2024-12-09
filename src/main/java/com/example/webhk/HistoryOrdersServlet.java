package com.example.webhk;

import org.json.JSONArray;
import org.json.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/HistoryOrdersServlet")
public class HistoryOrdersServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");

        String username = request.getParameter("username"); // 从 URL 中获取用户名
        String role = request.getParameter("role"); // 获取用户角色
        String dbUrl = "jdbc:mysql://localhost:3306/web?useSSL=false&serverTimezone=UTC";
        String dbUser = "root"; // MySQL 用户名
        String dbPassword = "123456"; // MySQL 密码

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            JSONArray ordersArray = new JSONArray();

            // 如果是管理员，查询所有用户的订单；如果是客户，查询当前用户的订单
            String orderSql;
            if ("order".equals(role)) {
                // 管理员，查询所有用户的订单
                orderSql = "SELECT o.OrderID, o.Username, o.OrderDate, o.TotalAmount " +
                        "FROM Orders o ORDER BY o.OrderDate DESC";
            } else {
                // 客户，查询当前用户的订单
                orderSql = "SELECT o.OrderID, o.Username, o.OrderDate, o.TotalAmount " +
                        "FROM Orders o WHERE o.Username = ? ORDER BY o.OrderDate DESC";
            }

            PreparedStatement orderStmt = conn.prepareStatement(orderSql);
            if (!"order".equals(role)) {
                orderStmt.setString(1, username);
            }
            ResultSet orderRs = orderStmt.executeQuery();

            while (orderRs.next()) {
                JSONObject order = new JSONObject();
                int orderId = orderRs.getInt("OrderID");
                order.put("OrderID", orderId);
                order.put("OrderDate", orderRs.getTimestamp("OrderDate"));
                order.put("TotalAmount", orderRs.getDouble("TotalAmount"));

                // 查询订单项
                String itemsSql = "SELECT oi.ProductName, oi.Quantity, oi.UnitPrice, oi.Subtotal " +
                        "FROM OrderItems oi WHERE oi.OrderID = ?";
                PreparedStatement itemsStmt = conn.prepareStatement(itemsSql);
                itemsStmt.setInt(1, orderId);
                ResultSet itemsRs = itemsStmt.executeQuery();

                JSONArray itemsArray = new JSONArray();
                while (itemsRs.next()) {
                    JSONObject item = new JSONObject();
                    item.put("ProductName", itemsRs.getString("ProductName"));
                    item.put("Quantity", itemsRs.getInt("Quantity"));
                    item.put("UnitPrice", itemsRs.getDouble("UnitPrice"));
                    item.put("Subtotal", itemsRs.getDouble("Subtotal"));
                    itemsArray.put(item);
                }

                order.put("Items", itemsArray);
                itemsStmt.close();

                if ("order".equals(role)) {
                    order.put("Username", orderRs.getString("Username")); // 添加用户名，适用于管理员
                }

                ordersArray.put(order);
            }

            response.getWriter().write(ordersArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
