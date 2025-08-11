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



<script>
// 폼 제출 전 체크박스 선택 여부 확인
function validateForm() {
    const checkboxes = document.querySelectorAll('input[name="productIds"]:checked');
    if (checkboxes.length === 0) {
        alert("삭제할 상품을 선택하세요.");
        return false; // 제출 막기
    }
    return true; // 제출 허용
}


</script>
</head>
<body>
    <h2>상품 조회</h2>
     <form action="productList.do" method="get">
	상품 ID : <input type = "text" name="product_id" value="${param.product_id}">
	상품명 : <input type = "text" name="product_name" value="${param.product_name}">
	<br>
	카테고리 : 
<select name="category">
    <option value="">-- 선택 --</option>
    <c:forEach items="${categoryList}" var="category">
        <option value="${category}" 
            <c:if test="${selectedCategory == category}">selected</c:if>>
            ${category}
        </option>
    </c:forEach>
</select>
	공급업체 ID  : <input type = "text" name="supplier_id" value="${param.supplier_id}">
	<br>

        <input type="submit" value="상품 조회">
	
	</form>

    <h2>상품 목록</h2>
     <form action="deleteProducts.do" method="post" onsubmit="return validateForm();">
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
            if(productList != null){
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
            <%
                }
                } else {
            %><tr><td colspan="6">조회된 상품이 없습니다..</td></tr>
            <% } %>
        </table>
        
        	<%
    
    			String productId = request.getParameter("product_id");
			    String productName = request.getParameter("product_name");
			    String category = request.getParameter("category");
			    String supplierId = request.getParameter("supplier_id");
			
			    boolean noResult = (productList == null || productList.isEmpty());
			    boolean allConditionsEmpty = ( (productId == null || productId.trim().isEmpty()) &&
                                  (productName == null || productName.trim().isEmpty()) &&
                                  (category == null || category.trim().isEmpty()) &&
                                  (supplierId == null || supplierId.trim().isEmpty()) );
			%>
	
		<% if (noResult && !allConditionsEmpty) { %>
			<script>
    			alert('조회된 상품이 없습니다.');
			</script>
		<% } %>
        
        <br>
        <input type="submit" value="선택 항목 삭제">
    </form>
           <br>
    <a href="addProduct.do">상품 등록하기</a>
</body>
</html>