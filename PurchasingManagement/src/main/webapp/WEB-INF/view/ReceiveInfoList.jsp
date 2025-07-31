<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.purchase.vo.ReceiveInfo" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>입고 리스트</title>
</head>
<body>
<h2>입고 품목</h2>
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
                List<ReceiveInfo> receiveInfoList = (List<ReceiveInfo>) request.getAttribute("receiveInfoList");
            if(receiveInfoList != null){
                for (ReceiveInfo p : receiveInfoList) {
            %>
            <tr>
                <td><input type="checkbox" name="productIds" value="<%= p.getProduct_id() %>"></td>
                <td><%= p.getReceive_id() %></td>
                <td><%= p.getOrder_id() %></td>
                <td><%= p.getProduct_id() %></td>
                <td><%= p.getQuantity() %></td>
                <td><%= p.getReceive_date() %></td>
            </tr>
            <%
                }
                } else {
            %><tr><td colspan="6">조회된 상품이 없습니다..</td></tr>
            <% } %>
        </table>
        
      <%--   	<%
    
    			String productId = request.getParameter("product_id");
			    String productName = request.getParameter("product_name");
			    String category = request.getParameter("category");
			    String supplierId = request.getParameter("supplier_id");
			
			    boolean noResult = (receiveInfoList == null || receiveInfoList.isEmpty());
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
         --%>
        <br>
        <input type="submit" value="선택 항목 삭제">
    </form>
           <br>
    <a href="addProduct.do">상품 등록하기</a>
</body>
</html>