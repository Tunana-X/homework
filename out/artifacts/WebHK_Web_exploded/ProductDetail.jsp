<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html>
<head>
  <title>商品详情</title>
  <script type="text/javascript">
    window.onload = function() {
      const urlParams = new URLSearchParams(window.location.search);
      let successMessage = urlParams.get('success');
      let warningMessage = urlParams.get('warning');
      if (successMessage) {
        successMessage = decodeURIComponent(successMessage);
        alert(successMessage);
      } else if (warningMessage) {
        warningMessage = decodeURIComponent(warningMessage);
        alert(warningMessage);
      }

      document.getElementById("quantity").addEventListener("input", updateTotalPrice);
    };

    function updateTotalPrice() {
      const unitPrice = parseFloat(document.getElementById("unitPrice").textContent);
      const quantity = parseInt(document.getElementById("quantity").value);
      const totalPrice = unitPrice * quantity;
      document.getElementById("totalPrice").textContent = totalPrice.toFixed(2);
    }

    // 删除商品前的弹窗确认
    function confirmDelete(productId) {
      if (confirm("确认要删除该商品吗？")) {
        window.location.href = "DeleteProductServlet?productId=" + productId;
      }
    }
  </script>
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
  <style>
    body {
      font-family: Arial, sans-serif;
      margin: 0;
      padding: 0;
    }
    .product-detail {
      display: flex;
      justify-content: center;
      padding: 20px;
    }
    .product-content {
      display: flex;
      flex-direction: row;
      gap: 30px;
    }
    .product-info, .edit-product-info {
      display: flex;
      flex-direction: column;
    }
    .edit-product-info {
      max-width: 300px;
      background-color: #f9f9f9;
      padding: 20px;
      border: 1px solid #ddd;
      border-radius: 8px;
    }
    .edit-product-info label,
    .edit-product-info input,
    .edit-product-info button {
      display: block;
      width: 100%;
      margin-bottom: 10px;
    }
    .product-image {
      width: 300px;
      height: 300px;
      object-fit: cover;
      margin-right: 30px;
    }
    .add-to-cart {
      background-color: #4CAF50;
      color: white;
      padding: 10px;
      border: none;
      cursor: pointer;
      font-size: 16px;
    }
    .add-to-cart:hover {
      background-color: #45a049;
    }
    .edit-product, .delete-product {
      color: white;
      padding: 10px;
      border: none;
      cursor: pointer;
      font-size: 16px;
      margin-top: 10px;
    }
    .edit-product {
      background-color: #ff9800;
    }
    .edit-product:hover {
      background-color: #f57c00;
    }
    .delete-product {
      background-color: #f44336;
    }
    .delete-product:hover {
      background-color: #d32f2f;
    }
    .chart-container {
      margin-top: 40px;
      padding: 20px;
      background-color: #fff;
      border: 1px solid #ddd;
      border-radius: 8px;
      width: 80%;
      max-width: 800px;
    }
    canvas {
      display: block;
      margin: 0 auto;
    }
  </style>
</head>
<body>
<a href="index.jsp" style="position: absolute; top: 10px; left: 10px;">
  返回首页
</a>

<%
  String productId = request.getParameter("productID");
  String dbUrl = "jdbc:mysql://localhost:3306/web?useSSL=false&serverTimezone=UTC"; // 修改为MySQL连接
  String dbUser = "root"; // MySQL 用户
  String dbPassword = "123456"; // MySQL 密码

  try {
    Class.forName("com.mysql.cj.jdbc.Driver"); // 使用 MySQL JDBC 驱动
    Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    String sql = "SELECT ProductName, ProductImage, UnitPrice, Quantity, ProductType FROM Products WHERE ProductID = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setInt(1, Integer.parseInt(productId));

    ResultSet rs = stmt.executeQuery();

    if (rs.next()) {
      String productName = rs.getString("ProductName");
      byte[] imageBytes = rs.getBytes("ProductImage");
      String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
      double unitPrice = rs.getDouble("UnitPrice");
      int quantity = rs.getInt("Quantity");
      String productType = rs.getString("ProductType");
%>

<div class="product-detail">
  <img class="product-image" src="data:image/jpeg;base64,<%= base64Image %>" alt="<%= productName %>">
  <div class="product-content">
    <div class="product-info">
      <h2><%= productName %></h2>
      <span><strong>价格：</strong>¥<span id="unitPrice"><%= unitPrice %></span></span>
      <span><strong>库存：</strong><%= quantity %></span>
      <span><strong>款式：</strong><%= productType %></span>

      <form id="addToCartForm" action="AddToCartServlet" method="post">
        <input type="hidden" name="productId" value="<%= productId %>">
        <input type="hidden" name="productName" value="<%= productName %>">
        <input type="hidden" name="unitPrice" value="<%= unitPrice %>">

        <label for="quantity">数量：</label>
        <input type="number" id="quantity" name="quantity" value="1" min="1" max="<%= quantity %>" required>

        <p>总金额：¥<span id="totalPrice"><%= unitPrice %></span></p>

        <button class="add-to-cart" type="submit">添加到购物车</button>
      </form>
    </div>

    <%
      HttpSession session1 = request.getSession(false);
      if (session1 != null && session1.getAttribute("username") != null) {
        String username = (String) session1.getAttribute("username");
        boolean isAdmin = false;

        try {
          String roleSql = "SELECT role FROM Users WHERE username = ?";
          PreparedStatement roleStmt = conn.prepareStatement(roleSql);
          roleStmt.setString(1, username);
          ResultSet roleRs = roleStmt.executeQuery();
          if (roleRs.next()) {
            String role = roleRs.getString("role");
            if ("order".equals(role)) {
              isAdmin = true;
            }
          }
          roleRs.close();
          roleStmt.close();
        } catch (Exception e) {
          e.printStackTrace();
        }

        if (isAdmin) {
    %>

    <div class="edit-product-info">
      <form id="editProductForm" action="EditProductServlet" method="post">
        <h3>修改商品信息</h3>
        <input type="hidden" name="productId" value="<%= productId %>">

        <label for="newPrice">新价格：</label>
        <input type="number" id="newPrice" name="newPrice" value="<%= unitPrice %>" min="0" required>

        <label for="newQuantity">新库存：</label>
        <input type="number" id="newQuantity" name="newQuantity" value="<%= quantity %>" min="0" required>

        <button class="edit-product" type="submit">确认修改</button>
      </form>

      <!-- 删除商品按钮 -->
      <button class="delete-product" onclick="confirmDelete('<%= productId %>')">删除商品</button>
    </div>

    <div class="chart-container">
      <h2 style="text-align: center;">按日期统计销售金额</h2>
      <canvas id="salesByDate"></canvas>
    </div>

    <script>
      const urlParams = new URLSearchParams(window.location.search);
      const productId = urlParams.get('productID');  // 获取 productID 参数

      if (productId) {
        // 输出 productId，确保它被正确获取
        console.log('Product ID:', productId);

        // 使用 productId 请求后端数据
        fetch("SalesReportByDateServlet?productID=" + productId)
                .then(response => {
                  if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                  }
                  return response.json();
                })
                .then(data => {
                  // 确保数据格式正确
                  console.log('Received data:', data);  // 输出数据到控制台，检查格式

                  // 提取日期和销售数据
                  const dateLabels = data.map(item => item.date);
                  const dateSales = data.map(item => item.sales);

                  // 获取今天的日期
                  const today = new Date().toISOString().split('T')[0];

                  // 如果今天没有数据，添加一个 sales = 0 的记录
                  if (!dateLabels.includes(today)) {
                    dateLabels.push(today);
                    dateSales.push(0);
                  }

                  // 渲染折线图
                  const ctx = document.getElementById("salesByDate").getContext("2d");
                  new Chart(ctx, {
                    type: 'line',  // 设置为折线图
                    data: {
                      labels: dateLabels,  // 横坐标为日期
                      datasets: [{
                        label: '销售金额',
                        data: dateSales,  // 销售金额数据
                        fill: false,  // 不填充折线下方的区域
                        borderColor: 'rgba(75, 192, 192, 1)',  // 折线颜色
                        tension: 0.1,  // 控制线条的弯曲度
                        pointBackgroundColor: 'rgba(75, 192, 192, 1)', // 点的颜色
                        pointRadius: 5,  // 点的大小
                        pointHoverRadius: 7,  // 鼠标悬停时点的大小
                      }]
                    },
                    options: {
                      responsive: true,
                      scales: {
                        y: {
                          beginAtZero: true,  // 确保 y 轴从 0 开始
                        }
                      },
                      plugins: {
                        legend: {
                          display: true,
                          position: 'top'
                        }
                      }
                    }
                  });
                })
                .catch(error => {
                  console.error('There has been a problem with your fetch operation:', error);
                });
      }
    </script>


    <%
        }
      }
    %>
  </div>
</div>

<%
    } else {
      out.println("商品不存在！");
    }
  } catch (Exception e) {
    e.printStackTrace();
  }
%>

</body>
</html>
