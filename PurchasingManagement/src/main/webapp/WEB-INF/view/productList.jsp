<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.purchase.vo.Product" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>상품 목록</title>
<style>
/* 테이블 스타일 */
table {
	width: 80%;
	border-collapse: collapse;
	margin: 20px auto;
}

th, td {
	border: 1px solid #ccc;
	padding: 8px;
	text-align: center;
}

th {
	background-color: #f2f2f2;
}

/* 버튼 스타일 */
.btn {
	padding: 4px 10px;
	background-color: #4CAF50;
	color: white;
	border: none;
	cursor: pointer;
	text-decoration: none;
}

.btn:hover {
	opacity: 0.8;
}

/* 상단 메뉴 레이아웃 */
.top-menu {
	width: 80%;
	margin: 20px auto;
	text-align: right;
}

.toolbar {
	width: 80%;
	margin: 10px auto;
	text-align: left;
}

/* 상태 배지 */
.badge-ok {
	padding: 2px 8px;
	border-radius: 10px;
	background: #e6f6e6;
}

.badge-stop {
	padding: 2px 8px;
	border-radius: 10px;
	background: #fdeaea;
}

.muted {
	opacity: .55;
}
</style>
<script>
function validateForm(){
    // 기존 기능 그대로
    return true;
}
</script>
</head>
<body>

<!-- 상단 메뉴 -->
<div class="top-menu">
    <a href="addProduct.do" class="btn">상품 등록</a>
    <a href="javascript:location.reload();" class="btn">재조회</a>
</div>

<h2 style="text-align:center;">상품 조회</h2>
<!-- 검색 폼 -->
<form action="searchProducts.do" method="get" style="width:80%; margin:20px auto; text-align:center;">
    상품 ID : <input type="text" name="product_id" value="${param.product_id}" style="margin-right:10px; padding:4px;">
    상품명 : <input type="text" name="product_name" value="${param.product_name}" style="margin-right:10px; padding:4px;">
    카테고리 :
    <select name="category" style="margin-right:10px; padding:4px;">
        <option value="">-- 선택 --</option>
        <c:forEach items="${categoryList}" var="cat">
            <option value="${cat}" <c:if test="${param.category == cat}">selected</c:if>>${cat}</option>
        </c:forEach>
    </select>
    공급업체 ID : <input type="text" name="supplier_id" value="${param.supplier_id}" style="margin-right:10px; padding:4px;">
    <input type="submit" value="검색" class="btn">
</form>
<!-- 상품 목록 테이블 -->
<h2 style="text-align:center;">상품 목록</h2>
<form action="updateProducts.do" method="post" onsubmit="return validateForm();">
    <table>
        <tr>
            <th>상품 ID</th>
            <th>상품명</th>
            <th>카테고리</th>
            <th>가격</th>
            <th>공급업체</th>
            <th>상태</th>
        </tr>
        <%
            List<Product> productList = (List<Product>) request.getAttribute("productList");
            if(productList != null){
                for (Product p : productList) {
        %>
        <tr class="<%= "단종".equals(p.getProduct_status()) ? "muted" : "" %>">
            <td><%= p.getProduct_id() %></td>
            <td><a href="modifyProduct.do?product_id=<%= p.getProduct_id() %>"><%= p.getProduct_name() %></a></td>
            <td><%= p.getCategory() %></td>
            <td><%= p.getPrice() %></td>
            <td><%= p.getSupplier_name() %> (<%= p.getSupplier_id() %>)</td>
            <td>
                <select name="<%= p.getProduct_id() %>">
                    <option value="정상판매" <%= "정상판매".equals(p.getProduct_status()) ? "selected" : "" %>>정상판매</option>
                    <option value="일시품절" <%= "일시품절".equals(p.getProduct_status()) ? "selected" : "" %>>일시품절</option>
                    <option value="예약판매" <%= "예약판매".equals(p.getProduct_status()) ? "selected" : "" %>>예약판매</option>
                    <option value="판매보류" <%= "판매보류".equals(p.getProduct_status()) ? "selected" : "" %>>판매보류</option>
                    <option value="단종" <%= "단종".equals(p.getProduct_status()) ? "selected" : "" %>>단종</option>
                </select>
            </td>
        </tr>
        <% } } else { %>
        <tr><td colspan="6">조회된 상품이 없습니다.</td></tr>
        <% } %>
    </table>
    <br>
    <input type="submit" class="btn" value="상태 저장">
</form>
</body>
</html>