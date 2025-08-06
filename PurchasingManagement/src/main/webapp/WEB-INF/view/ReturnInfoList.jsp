<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.purchase.vo.ReturnInfo" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>



<script>
// 폼 제출 전 체크박스 선택 여부 확인
function validateForm() {
    const checkboxes = document.querySelectorAll('input[name="returnInfoIds"]:checked');
    if (checkboxes.length === 0) {
        alert("삭제할 반품 정보를 선택하세요.");
        return false; // 제출 막기
    }
    return true; // 제출 허용
}
</script>
</head>
<body>
    <h2>입고 정보 조회</h2>
<%--      <form action="returnInfoList.do" method="get">
	반품 고유 번호 : <input type = "text" name="return_id" value="${param.product_id}">
	<br>
	입고 고유 번호 : <input type = "text" name="receive_id" value="${param.product_name}">
	<br>
	상품 고유 번호 : <input type = "text" name="product_id" value="${param.category}">
	<br>

        <input type="submit" value="반품 정보 조회">
	
	</form>
 --%>
    <h2>반품  정보</h2>
     <form action="deleteReturnInfos.do" method="post" onsubmit="return validateForm();">
        <table border="1">
            <tr>
                <th>선택</th>
                <th>반품 고유 번호</th>
                <th>입고 고유 번호</th>
                <th>상품 고유 번호</th>
                <th>반품 수량</th>
                <th>반품 사유</th>
                <th>반품 날짜</th>
            </tr>
            <%
                List<ReturnInfo> returnInfoList = (List<ReturnInfo>) request.getAttribute("returnInfoList");
            if(returnInfoList != null){
                for (ReturnInfo r : returnInfoList) {
            %>
            <tr>
                <td><input type="checkbox" name="returnInfoIds" value="<%= r.getReturn_id() %>"></td>
                <td><%= r.getReturn_id() %></td>
                <td><%= r.getReceive_id() %></td>
                <td><%= r.getProduct_id() %></td>
                <td><%= r.getQuantity() %></td>
                <td><%= r.getReason() %></td>
                <td><%= r.getReturn_date() %></td>
            </tr>
            <%
                }
                } else {
            %><tr><td colspan="6">조회된 상품이 없습니다..</td></tr>
            <% } %>
        </table>
        
        	<%
    
    			String returnId = request.getParameter("return_id");
			    String receiveId = request.getParameter("receive_id");
			    String productId = request.getParameter("product_id");
			    
			
			    boolean noResult = (returnInfoList == null || returnInfoList.isEmpty());
			    boolean allConditionsEmpty = ( (productId == null || productId.trim().isEmpty()) &&
                                  (returnId == null || returnId.trim().isEmpty()) &&
                                  (receiveId == null || receiveId.trim().isEmpty()) &&
                                  (productId == null || productId.trim().isEmpty()) );
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
   <!--  <a href="addProduct.do">상품 반품하기</a> -->
</body>
</html>