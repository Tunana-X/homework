<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*, java.util.*" %>
<!DOCTYPE html>
<html>
<head>
  <title>我的购物车</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      margin: 0;
      padding: 0;
    }
    h1 {
      text-align: center;
      margin: 20px 0;
    }
    .cart-container {
      margin: 20px auto;
      width: 80%;
      max-width: 800px;
      border: 1px solid #ddd;
      border-radius: 5px;
      padding: 20px;
      background-color: #f9f9f9;
    }
    .cart-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      padding: 10px;
      border-bottom: 1px solid #ddd;
    }
    .cart-item:last-child {
      border-bottom: none;
    }
    .cart-item img {
      width: 100px;
      height: 100px;
      object-fit: cover;
      border-radius: 5px;
    }
    .cart-info {
      flex: 1;
      margin-left: 20px;
    }
    .cart-info span {
      display: block;
      margin-bottom: 5px;
    }
    .cart-actions {
      display: flex;
      align-items: center;
      gap: 10px;
    }
    .cart-actions button {
      background-color: #d9534f;
      color: white;
      border: none;
      padding: 5px 10px;
      border-radius: 5px;
      cursor: pointer;
    }
    .cart-actions button:hover {
      background-color: #c9302c;
    }
    .cart-total {
      text-align: right;
      font-size: 18px;
      margin-top: 20px;
      font-weight: bold;
    }
    .cart-total span {
      color: #d9534f;
    }
  </style>
  <script>
    // 实时更新总金额
    function updateTotalPrice() {
      let total = 0;
      const checkboxes = document.querySelectorAll('.select-item:checked');
      checkboxes.forEach((checkbox) => {
        let row = checkbox.closest('.cart-item');
        let unitPrice = parseFloat(row.querySelector('.unit-price').textContent.replace('¥', ''));
        let quantity = parseInt(row.querySelector('.quantity-input').value);
        let subtotal = unitPrice * quantity;
        row.querySelector('.subtotal-price').textContent = subtotal.toFixed(2);
        total += subtotal;
      });
      document.getElementById("totalPrice").textContent = total.toFixed(2);
    }

    // 删除购物车项
    function deleteCartItem(cartItemId) {
      const data = {
        cartItemId: cartItemId
      };

      fetch('DeleteCartItemServlet', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      })
              .then(response => response.text())
              .then(message => {
                if (message === 'Success') {
                  // 删除成功后刷新页面
                  window.location.reload();
                } else {
                  alert(message);
                }
              })
              .catch(error => console.error('Error:', error));
    }

    // 修改购物车中的商品数量并同步到数据库
    function updateQuantity(cartItemId123, newQuantity123) {
      console.log('cartItemId123:', cartItemId123);
      console.log('newQuantity123:', newQuantity123);

      if (cartItemId123 && newQuantity123) {
        fetch('UpdateCartItemServlet', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            cartItemId: cartItemId123,
            quantity: newQuantity123,
          }),
        })
                .then(response => response.text())
                .then(message => {
                  if (message === 'Success') {
                    console.log('Update successful');
                  } else {
                    alert(message); // 弹出失败信息
                  }
                })
                .catch(error => console.error('Error:', error));
      } else {
        console.error('Invalid parameters:', cartItemId123, newQuantity123);
      }
    }

    //结算购物车
    function checkoutCart(username) {
      // 创建一个包含用户名的 JSON 对象
      const data = {
        username: username
      };

      fetch('CheckoutServlet', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),  // 将数据转换为 JSON 字符串
      })
              .then(response => response.text())
              .then(message => {
                if (message === 'Checkout successful') {
                  alert('结算成功！');
                  window.location.reload();
                } else {
                  alert(message);
                }
              })
              .catch(error => console.error('Error:', error));
    }

    // 页面加载时弹出消息并绑定事件
    window.onload = function() {
      // 第一个功能：弹出成功消息
      const urlParams = new URLSearchParams(window.location.search);
      let successMessage = urlParams.get('success');
      let warningMessage = urlParams.get('warning');
      if (successMessage) {
        successMessage = decodeURIComponent(successMessage);  // 解码
        alert(successMessage);  // 弹窗显示成功消息
      } else if(warningMessage){
        warningMessage = decodeURIComponent(warningMessage);
        alert(warningMessage);
      }

      // 第二个功能：更新总金额
      const quantityInputs = document.querySelectorAll('.quantity-input');
      quantityInputs.forEach(input => {
        input.addEventListener("input", updateTotalPrice);
      });

      // 初始化总金额
      updateTotalPrice();
    }
  </script>
</head>
<body>
<h1>我的购物车</h1>
<a href="index.jsp" style="position: absolute; top: 10px; left: 10px;">
  返回首页
</a>
<div class="cart-container">
  <%
    HttpSession session1 = request.getSession(false);
    if (session1 != null && session1.getAttribute("username") != null) {
      String username = (String) session1.getAttribute("username");
      String dbUrl = "jdbc:mysql://localhost:3306/web?useSSL=false&serverTimezone=UTC";
      String dbUser = "root";
      String dbPassword = "123456";

      try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

        String sql = "SELECT ci.CartItemID, ci.ProductName, ci.UnitPrice, ci.Quantity, ci.SubtotalPrice, p.ProductImage " +
                "FROM ShoppingCart sc " +
                "JOIN CartItems ci ON sc.CartID = ci.CartID " +
                "JOIN Products p ON ci.ProductName = p.ProductName " +
                "WHERE sc.Username = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();

        double totalPrice = 0.0;
        while (rs.next()) {
          int cartItemId = rs.getInt("CartItemID");
          String productName = rs.getString("ProductName");
          double unitPrice = rs.getDouble("UnitPrice");
          int quantity = rs.getInt("Quantity");
          double subtotalPrice = rs.getDouble("SubtotalPrice");
          byte[] imageBytes = rs.getBytes("ProductImage");
          String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);

          totalPrice += subtotalPrice;
  %>
  <div class="cart-item" data-cart-item-id="<%= cartItemId %>">
    <img src="data:image/jpeg;base64,<%= base64Image %>" alt="<%= productName %>">
    <div class="cart-info">
      <span><strong>名称：</strong><%= productName %></span>
      <span><strong>单价：</strong>¥<span class="unit-price"><%= unitPrice %></span></span>
      <span><strong>小计：</strong>¥<span class="subtotal-price"><%= subtotalPrice %></span></span>
    </div>
    <div class="cart-actions">
      <input type="number" class="quantity-input" value="<%= quantity %>" min="1" onclick="updateQuantity(<%=cartItemId%>,this.value)">
      <input type="checkbox" class="select-item" checked onclick="updateTotalPrice()">
      <button type="button" onclick="deleteCartItem(<%= cartItemId %>)">删除</button>
    </div>
  </div>
  <%
    }
    rs.close();
    stmt.close();
    conn.close();
  %>
  <div class="cart-total">
    <button type="button" onclick="checkoutCart('<%=username%>')">结算</button>
    总计金额：¥<span id="totalPrice"><%= totalPrice %></span>
  </div>
  <%
      } catch (Exception e) {
        e.printStackTrace();
        out.println("<p>加载购物车失败，请稍后重试。</p>");
      }
    } else {
      out.println("<p>请先登录以查看购物车。</p>");
    }
  %>
</div>
</body>
</html>
