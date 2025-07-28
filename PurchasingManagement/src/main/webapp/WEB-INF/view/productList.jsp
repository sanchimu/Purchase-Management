<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.purchase.vo.Product" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>상품 목록</title>
</head>
<body>
    <h2>상품 목록</h2>
    <form action="deleteProducts.do" method="post">
        <table border="1">
            <tr>
                <th>선택</th>
                <th>상품 ID</th>
                <th>상품명</th>
                <th>카테고리</th>
                <th>가격</th>
                <th>공급업체</th>
            </tr>
            <%
                List<Product> productList = (List<Product>) request.getAttribute("productList");
                for (Product p : productList) {
            %>
            <tr>
                <td><input type="checkbox" name="productIds" value="<%= p.getProduct_id() %>"></td>
                <td><%= p.getProduct_id() %></td>
                <td><%= p.getProduct_name() %></td>
                <td><%= p.getCategory() %></td>
                <td><%= p.getPrice() %></td>
                <td><%= p.getSupplier_id() %></td>
            </tr>
            <% } %>
        </table>
        <br>
        <input type="submit" value="선택 항목 삭제">
    </form>
</body>
</html>