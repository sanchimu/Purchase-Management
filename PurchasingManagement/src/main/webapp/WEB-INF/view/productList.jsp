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
<title>商品目録</title>
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
    <a href="addProduct.do" class="btn">商品登録</a>
    <a href="javascript:location.reload();" class="btn">再照会</a>
</div>

<h2 style="text-align:center;">商品照会</h2>
<!-- 검색 폼 -->
<form action="searchProducts.do" method="get" style="width:80%; margin:20px auto; text-align:center;">
    商品ID : <input type="text" name="product_id" value="${param.product_id}" style="margin-right:10px; padding:4px;">
    商品名 : <input type="text" name="product_name" value="${param.product_name}" style="margin-right:10px; padding:4px;">
    分類 :
    <select name="category" style="margin-right:10px; padding:4px;">
        <option value="">-- 選択 --</option>
        <c:forEach items="${categoryList}" var="cat">
            <option value="${cat}" <c:if test="${param.category == cat}">selected</c:if>>${cat}</option>
        </c:forEach>
    </select>
    仕入先ID : <input type="text" name="supplier_id" value="${param.supplier_id}" style="margin-right:10px; padding:4px;">
    <input type="submit" value="검색" class="btn">
</form>
<!-- 상품 목록 테이블 -->
<h2 style="text-align:center;">商品目録</h2>
<form action="updateProducts.do" method="post" onsubmit="return validateForm();">
    <table>
        <tr>
            <th>商品ID</th>
            <th>商品名</th>
            <th>分類</th>
            <th>価格</th>
            <th>仕入先</th>
            <th>状態</th>
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
                    <option value="販売中" <%= "販売中".equals(p.getProduct_status()) ? "selected" : "" %>>販売中</option>
                    <option value="一時欠品" <%= "一時欠品".equals(p.getProduct_status()) ? "selected" : "" %>>一時欠品</option>
                    <option value="予約販売" <%= "予約販売".equals(p.getProduct_status()) ? "selected" : "" %>>予約販売</option>
                    <option value="販売停止" <%= "販売停止".equals(p.getProduct_status()) ? "selected" : "" %>>販売停止</option>
                    <option value="販売終了" <%= "販売終了".equals(p.getProduct_status()) ? "selected" : "" %>>販売終了</option>
                </select>
            </td>
        </tr>
        <% } } else { %>
        <tr><td colspan="6">照会された商品がありません。</td></tr>
        <% } %>
    </table>
    <br>
    <input type="submit" class="btn" value="状態変更保存">
</form>
</body>
</html>