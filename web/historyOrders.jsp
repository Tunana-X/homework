<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>历史订单</title>
    <style>
        body {
            text-align: center;
            font-family: Arial, sans-serif;
        }

        #orders-container {
            display: flex;
            flex-direction: column;
            align-items: center;
            margin-top: 20px;
        }

        a {
            position: absolute;
            top: 10px;
            left: 10px;
            font-size: 16px;
        }

        h1 {
            text-align: center;
        }

        table {
            margin-top: 20px;
            width: 80%;
            margin-left: auto;
            margin-right: auto;
            border-collapse: collapse;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: center;
        }

        th {
            background-color: #f2f2f2;
        }
    </style>
    <script>
        function loadHistoryOrders(username, isAdmin) {
            let url = `HistoryOrdersServlet?username=${username}`;
            if (isAdmin) {
                url += "&role=order"; // 传递role参数，用于区分管理员请求
            }

            fetch(url)
                .then(response => response.json())
                .then(data => {
                    const container = document.getElementById('orders-container');
                    container.innerHTML = '';

                    if (data.error) {
                        container.textContent = '加载历史订单失败：' + data.error;
                        return;
                    }

                    data.forEach(order => {
                        const orderDiv = document.createElement('div');
                        orderDiv.innerHTML =
                            '<h3>订单号：' + order.OrderID + '</h3>' +
                            '<p>时间：' + new Date(order.OrderDate).toLocaleString() + '</p>' +
                            '<p>总金额：¥' + order.TotalAmount.toFixed(2) + '</p>';

                        if (isAdmin) {
                            orderDiv.innerHTML += '<p>订单用户：' + order.Username + '</p>';  // 显示订单的用户名
                        }

                        orderDiv.innerHTML +=
                            '<table border="1">' +
                            '<thead>' +
                            '<tr>' +
                            '<th>商品名</th>' +
                            '<th>数量</th>' +
                            '<th>单价</th>' +
                            '<th>小计</th>' +
                            '</tr>' +
                            '</thead>' +
                            '<tbody>' +
                            order.Items.map(item => {
                                return (
                                    '<tr>' +
                                    '<td>' + item.ProductName + '</td>' +
                                    '<td>' + item.Quantity + '</td>' +
                                    '<td>¥' + item.UnitPrice.toFixed(2) + '</td>' +
                                    '<td>¥' + item.Subtotal.toFixed(2) + '</td>' +
                                    '</tr>'
                                );
                            }).join('') +
                            '</tbody>' +
                            '</table>';
                        container.appendChild(orderDiv);
                    });
                })
                .catch(error => console.error('Error:', error));
        }

        window.onload = function() {
            const username = '<%= request.getAttribute("username") %>';

            <%-- 通过session获取用户角色 --%>
            <%
                HttpSession session1 = request.getSession(false);
                boolean isAdmin = false;
                if (session1 != null && session1.getAttribute("username") != null) {
                    String loggedInUsername = (String) session1.getAttribute("username");
                    String dbUrl = "jdbc:mysql://localhost:3306/web?useSSL=false&serverTimezone=UTC"; // MySQL连接
                    String dbUser = "root";
                    String dbPassword = "123456"; // 根据实际情况修改密码
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                        String roleSql = "SELECT role FROM Users WHERE username = ?";
                        PreparedStatement stmt = conn.prepareStatement(roleSql);
                        stmt.setString(1, loggedInUsername);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            String role = rs.getString("role");
                            if ("order".equals(role)) {
                                isAdmin = true; // 如果是管理员，设置isAdmin为true
                            }
                        }
                        rs.close();
                        stmt.close();
                        conn.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            %>

            // 将isAdmin的值传递给JavaScript
            loadHistoryOrders(username, <%= isAdmin ? "true" : "false" %>);
        };
    </script>
</head>
<body>
<a href="index.jsp" style="position: absolute; top: 10px; left: 10px;">返回首页</a>
<h1>历史订单</h1>
<div id="orders-container"></div>
</body>
</html>
