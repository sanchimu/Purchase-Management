<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.purchase.vo.ReceiveInfo" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>返品要請</title>
<style>
body {
    font-family: Arial, sans-serif;
}

/* 제목 중앙 정렬 */
h3 {
    text-align: center;
}

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

/* 폼 스타일 */
form {
    width: 50%;
    margin: 20px auto;
    padding: 15px;
    border: 1px solid #ccc;
    border-radius: 8px;
    background-color: #f9f9f9;
}

/* 입력 필드 */
input[type="text"] {
    width: 100%;
    padding: 6px 8px;
    margin-top: 4px;
    margin-bottom: 10px;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-sizing: border-box;
}

/* 버튼 스타일 */
input[type="submit"],
input[type="button"] {
    padding: 6px 12px;
    margin-right: 10px;
    background-color: #4CAF50;
    border: none;
    border-radius: 4px;
    color: white;
    cursor: pointer;
}

input[type="submit"]:hover,
input[type="button"]:hover {
    opacity: 0.85;
}
</style>
<script>
window.onload = function() {
    <% if (request.getAttribute("success") != null) { %>
        alert("返品要請完了");
        document.querySelector("form").reset();
    <% } %>
}
</script>
</head>
<body>

<h3>入庫現況</h3>

<form action="returnProcess.do" method="post">
<table>
    <tr>
        <th>選択</th>
        <th>入庫番号</th>
        <th>注文番号</th>
        <th>商品名(商品番号)</th>
        <th>入庫数量</th>
        <th>返品可能数量</th>
        <th>入庫日</th>
    </tr>
<%
List<ReceiveInfo> receiveInfoList = (List<ReceiveInfo>) request.getAttribute("receiveInfoList");
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

if (receiveInfoList != null && !receiveInfoList.isEmpty()) {
    for (ReceiveInfo info : receiveInfoList) {
        int available = info.getAvailable_to_return();
%>
<tr>
    <td>
        <% if (available > 0) { %>
            <input type="radio" name="receiveId" value="<%= info.getReceive_id() %>">
        <% } else { %>
            <input type="radio" disabled>
        <% } %>
    </td>
    <td><%= info.getReceive_id() %></td>
    <td><%= info.getOrder_id() %></td>
    <td><%= info.getProduct_id() %></td>
    <td><%= info.getQuantity() %></td>
    <td><%= available %></td>
    <td><%= info.getReceive_date() != null ? sdf.format(info.getReceive_date()) : "" %></td>
</tr>
<%
    }
} else {
%>
<tr>
    <td colspan="7" style="text-align:center;">返品可能な入荷品はございません。</td>
</tr>
<%
}
%>
</table>

<p>
    返品数量 : <input type="text" name="quantity"/>
    返品事由 : <input type="text" name="reason"/>
</p>
<p>
    <input type="submit" value="반품 요청">
    <input type="button" value="뒤로 가기" onclick="location.href='returnInfoList.do'">
</p>
</form>

</body>
</html>