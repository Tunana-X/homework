<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.*" %>
<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html>
<head>
  <title>添加商品</title>
  <style>
    /* 样式定义 */
    body {
      font-family: Arial, sans-serif;
      margin: 0;
      padding: 0;
    }
    .form-container {
      width: 400px;
      padding: 20px;
      margin: 0 auto;
      border: 1px solid #ddd;
      border-radius: 5px;
      box-shadow: 0 0 10px rgba(0,0,0,0.1);
    }
    .form-container input, .form-container button {
      width: 100%;
      padding: 10px;
      margin: 5px 0;
      box-sizing: border-box;
    }
    .product-image-preview {
      width: 100px;
      height: 100px;
      object-fit: cover;
      margin-top: 10px;
    }
  </style>
</head>
<body>
<a href="index.jsp" style="position: absolute; top: 10px; left: 10px;">
  返回首页
</a>
<div class="form-container">
  <h2>添加商品</h2>
  <form action="AddProductServlet" method="post" enctype="multipart/form-data">
    <label for="productName">商品名称</label>
    <input type="text" name="productName" required><br>

    <label for="productImage">商品图片</label>
    <input type="file" name="productImage" accept="image/jpeg" onchange="previewImage(event)" required><br>
    <img id="imagePreview" class="product-image-preview" src="" alt="商品图片预览"><br>

    <label for="unitPrice">商品价格</label>
    <input type="number" name="unitPrice" step="0.01" min="0" required><br>

    <label for="quantity">商品库存</label>
    <input type="number" name="quantity" min="1" required><br>

    <label for="productType">商品类型</label>
    <input type="text" name="productType" required><br>

    <button type="submit">提交</button>
  </form>
</div>

<script>
  function previewImage(event) {
    var reader = new FileReader();
    reader.onload = function() {
      var output = document.getElementById('imagePreview');
      output.src = reader.result;
    };
    reader.readAsDataURL(event.target.files[0]);
  }
</script>
</body>
</html>
