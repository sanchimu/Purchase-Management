<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>발주서 작성</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .header {
            text-align: center;
            border-bottom: 2px solid #333;
            padding-bottom: 20px;
            margin-bottom: 30px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: inline-block;
            width: 120px;
            font-weight: bold;
            color: #333;
        }
        input[type="text"], input[type="date"], input[type="number"], select {
            width: 200px;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }
        .product-section {
            border: 1px solid #ddd;
            padding: 20px;
            margin: 20px 0;
            border-radius: 5px;
            background-color: #f9f9f9;
        }
        .product-item {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
            padding: 10px;
            background: white;
            border-radius: 4px;
        }
        .product-item input {
            margin-right: 10px;
            width: 150px;
        }
        .btn {
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            margin: 5px;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .btn-secondary {
            background-color: #6c757d;
        }
        .btn-secondary:hover {
            background-color: #545b62;
        }
        .success {
            color: green;
            padding: 10px;
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .error {
            color: red;
            padding: 10px;
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            border-radius: 4px;
            margin-bottom: 20px;
        }
    </style>
    <script>
        let productCount = 1;
        
        function addProduct() {
            productCount++;
            const productSection = document.getElementById('productSection');
            const newProduct = document.createElement('div');
            newProduct.className = 'product-item';
            newProduct.innerHTML = `
                <input type="text" name="product_id_${productCount}" placeholder="상품 ID" required>
                <input type="text" name="product_name_${productCount}" placeholder="상품명" required>
                <input type="text" name="category_${productCount}" placeholder="카테고리" required>
                <input type="number" name="quantity_${productCount}" placeholder="수량" min="1" required>
                <input type="number" name="price_${productCount}" placeholder="단가" min="0" step="0.01" required>
                <button type="button" class="btn btn-secondary" onclick="removeProduct(this)">삭제</button>
            `;
            productSection.appendChild(newProduct);
            document.getElementById('productCount').value = productCount;
        }
        
        function removeProduct(button) {
            button.parentElement.remove();
        }
        
        function validateForm() {
            const requiredFields = document.querySelectorAll('input[required], select[required]');
            for (let field of requiredFields) {
                if (!field.value.trim()) {
                    alert('모든 필수 항목을 입력해주세요.');
                    field.focus();
                    return false;
                }
            }
            return true;
        }
    </script>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>발주서 작성</h1>
            <p>Purchase Order Form</p>
        </div>

        <form action="purchaseOrderProcess.jsp" method="post" onsubmit="return validateForm()">
            <div class="form-group">
                <label for="requester_name">요청자명:</label>
                <input type="text" id="requester_name" name="requester_name" required>
            </div>
            
            <div class="form-group">
                <label for="request_date">요청날짜:</label>
                <input type="date" id="request_date" name="request_date" 
                       value="<%= new SimpleDateFormat("yyyy-MM-dd").format(new Date()) %>" required>
            </div>
            
            <div class="form-group">
                <label for="supplier_id">공급업체 ID:</label>
                <input type="text" id="supplier_id" name="supplier_id" placeholder="예: SUP001" required>
            </div>
            
            <div class="form-group">
                <label for="order_date">발주날짜:</label>
                <input type="date" id="order_date" name="order_date" 
                       value="<%= new SimpleDateFormat("yyyy-MM-dd").format(new Date()) %>" required>
            </div>
            
            <div class="product-section">
                <h3>발주 상품 목록</h3>
                <div id="productSection">
                    <div class="product-item">
                        <input type="text" name="product_id_1" placeholder="상품 ID (예: P0001)" required>
                        <input type="text" name="product_name_1" placeholder="상품명" required>
                        <input type="text" name="category_1" placeholder="카테고리" required>
                        <input type="number" name="quantity_1" placeholder="수량" min="1" required>
                        <input type="number" name="price_1" placeholder="단가" min="0" step="0.01" required>
                    </div>
                </div>
                <button type="button" class="btn btn-secondary" onclick="addProduct()">상품 추가</button>
            </div>
            
            <input type="hidden" id="productCount" name="productCount" value="1">
            
            <div style="text-align: center; margin-top: 30px;">
                <button type="submit" class="btn">발주서 등록</button>
                <button type="reset" class="btn btn-secondary">초기화</button>
                <a href="purchaseOrderList.jsp" class="btn btn-secondary">발주서 목록</a>
            </div>
        </form>
    </div>
</body>
</html>