<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="com.purchase.service.PurchaseOrderService" %>
<%@ page import="com.purchase.vo.*" %>

<%
    request.setCharacterEncoding("UTF-8");
    
    String message = "";
    boolean success = false;
    
    try {
        // 폼 데이터 수집
        String requesterName = request.getParameter("requester_name");
        String requestDateStr = request.getParameter("request_date");
        String supplierId = request.getParameter("supplier_id");
        String orderDateStr = request.getParameter("order_date");
        int productCount = Integer.parseInt(request.getParameter("productCount"));
        
        // 날짜 변환
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date requestDate = sdf.parse(requestDateStr);
        Date orderDate = sdf.parse(orderDateStr);
        
        // 상품 목록 수집
        List<PurchaseRequestItem> items = new ArrayList<>();
        
        for (int i = 1; i <= productCount; i++) {
            String productId = request.getParameter("product_id_" + i);
            String productName = request.getParameter("product_name_" + i);
            String category = request.getParameter("category_" + i);
            String quantityStr = request.getParameter("quantity_" + i);
            String priceStr = request.getParameter("price_" + i);
            
            if (productId != null && !productId.trim().isEmpty()) {
                PurchaseRequestItem item = new PurchaseRequestItem();
                item.setProductId(productId);
                item.setQuantity(Integer.parseInt(quantityStr));
                
                // 상품 정보 설정
                Product product = new Product();
                product.setProductId(productId);
                product.setProductName(productName);
                product.setCategory(category);
                product.setPrice(Double.parseDouble(priceStr));
                product.setSupplierId(supplierId);
                
                item.setProduct(product);
                items.add(item);
            }
        }
        
        // 서비스 호출
        PurchaseOrderService service = new PurchaseOrderService();
        success = service.createPurchaseOrder(requesterName, requestDate, supplierId, orderDate, items);
        
        if (success) {
            message = "발주서가 성공적으로 등록되었습니다.";
        } else {
            message = "발주서 등록에 실패했습니다.";
        }
        
    } catch (Exception e) {
        message = "오류가 발생했습니다: " + e.getMessage();
        e.printStackTrace();
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>발주서 처리 결과</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            text-align: center;
        }
        .success {
            color: green;
            padding: 20px;
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .error {
            color: red;
            padding: 20px;
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .btn {
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            margin: 5px;
            display: inline-block;
        }
        .btn:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>발주서 처리 결과</h2>
        
        <div class="<%= success ? "success" : "error" %>">
            <%= message %>
        </div>
        
        <div>
            <a href="purchaseOrderForm.jsp" class="btn">새 발주서 작성</a>
            <a href="purchaseOrderList.jsp" class="btn">발주서 목록</a>
        </div>
    </div>
</body>
</html>