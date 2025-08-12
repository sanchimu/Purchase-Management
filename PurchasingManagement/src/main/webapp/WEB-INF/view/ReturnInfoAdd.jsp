<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.purchase.vo.ReceiveInfo" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script>
window.onload = function() {
    // 서버에서 전달된 success 플래그가 true일 경우 alert 띄우기
    <% if (request.getAttribute("success") != null) { %>
        alert("반품 요청 완료");
        // 입력 필드 초기화 (방법 1: form 전체 reset)
        document.querySelector("form").reset();
    <% } %>
}
</script>
<title>반품 요청</title>
</head>


<body>
    <%
        // 컨트롤러에서 전달받은 입고 목록
        List<ReceiveInfo> receiveInfoList = (List<ReceiveInfo>) request.getAttribute("receiveInfoList");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    %>

		<h3>입고 현황</h3>
		<form action = returnProcess.do method="post">
 <table border="1">
            <tr>
                <th>선택</th>
                <th>입고 ID</th>
                <th>주문 ID</th>
                <th>상품 ID</th>
                <th>수량</th>
                <th>반품 가능 수량</th>
                <th>입고일자</th>
            </tr>

<%
if (receiveInfoList != null && !receiveInfoList.isEmpty()) {
    for (ReceiveInfo info : receiveInfoList) {
        int available = info.getAvailable_to_return();
%>
    <tr>
        <td>
            <% if (available > 0) { %>
                <input type="radio" name="receiveId" value="<%= info.getReceive_id() %>">
            <% } else { %>
                <!-- 반품 가능 수량 없으면 체크 불가 -->
                <input type="radio" disabled>
            <% } %>
        </td>
        <td><%= info.getReceive_id() %></td>
        <td><%= info.getOrder_id() %></td>
        <td><%= info.getProduct_id() %></td>
        <td><%= info.getQuantity() %></td>
        <td><%= available %></td>
        <td><%= info.getReceive_date() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(info.getReceive_date()) : "" %></td>
    </tr>
<%
        }
    } else {
%>
    <tr>
        <td colspan="7">반품 가능한 입고 리스트가 없습니다.</td>
    </tr>
<%
    }
%>

        </table>

<p>
	반품 수량 : <input type="text" name="quantity"/><br>
	반품 사유 : <input type="text" name="reason"/><br><br>
	<input type = "submit" value="반품 요청">
	<input type="button" value="뒤로 가기" onclick="location.href='returnInfoList.do'">
	</form>
</p>
</body>
</html>