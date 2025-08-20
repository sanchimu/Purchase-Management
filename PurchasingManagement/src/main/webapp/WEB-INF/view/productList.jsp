<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.purchase.vo.Product" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<c:choose>
  <c:when test="${empty categoryList}">
    <p style="color:red;">categoryList가 없습니다.</p>
  </c:when>
  <c:otherwise>
    <p>categoryList 크기: ${fn:length(categoryList)}</p>
  </c:otherwise>
</c:choose>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>상품 목록</title>
</head>
<body>
    <h2>상품 조회</h2>
     <form action="searchProducts.do" method="get">
	상품 ID : <input type = "text" name="product_id" value="${param.product_id}">
	상품명 : <input type = "text" name="product_name" value="${param.product_name}">
	<br>
	카테고리 : 
<select name="category">
    <option value="">-- 선택 --</option>
    <c:forEach items="${categoryList}" var="cat">
        <option value="${cat}" 
            <c:if test="${param.category == cat}">selected</c:if>>
            ${cat}
        </option>
    </c:forEach>
</select>
	공급업체 ID  : <input type = "text" name="supplier_id" value="${param.supplier_id}">
	<br>

        <input type="submit" value="상품 조회">
	
	</form>

    <h2>상품 목록</h2>
     <form action="updateProducts.do" method="post" onsubmit="return validateForm();">
          <% Boolean NoSearchProductResult = (Boolean) request.getAttribute("NoSearchProductResult");
   		if (Boolean.TRUE.equals(NoSearchProductResult)) { %>
    <script>alert("조회된 상품이 없습니다. 전체 목록을 표시합니다.");</script>
	<% } %>
        <table border="1">
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
            <tr>
                <td><%= p.getProduct_id() %></td>
                <td><%= p.getProduct_name() %></td>
                <td><%= p.getCategory() %></td>
                <td><%= p.getPrice() %></td>
                <td><%= p.getSupplier_id() %></td>
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
            <%
                }
                } else {
            %><tr><td colspan="6">조회된 상품이 없습니다..</td></tr>
            <% } %>
        </table>
        <br>
        <input type="submit" value="상태 저장">
    </form>
           <br>
    <a href="addProduct.do">상품 등록하기</a>
</body>
</html>