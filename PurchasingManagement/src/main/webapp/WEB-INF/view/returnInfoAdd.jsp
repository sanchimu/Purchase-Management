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
    white-space: nowrap; /* 줄바꿈 없이 글자 크기에 맞춤 */
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
window.onload = function() { //경고창을 띄우기 위한 함수
    <% if (request.getAttribute("success") != null) { %> //success값이 있을경우 경고창 발생 (alert)
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
        <th>入庫ID</th>
        <th>発注ID</th>
        <th>商品名(商品ID)</th>
        <th>入庫数量</th>
        <th>返品可能数量</th>
        <th>入庫日</th>
    </tr>
<%
List<ReceiveInfo> receiveInfoList = (List<ReceiveInfo>) request.getAttribute("receiveInfoList"); // receiveInfoList 키에 저장된 값을 가져와서 List<ReceiveInfo>형으로 변환해서 receiveInfoList 객체에 저장 */
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 날짜 형태의 포맷으로 변환하기 위한 객체 선언

if (receiveInfoList != null && !receiveInfoList.isEmpty()) { //receiveInfoList가 있을 경우 (입고 리스트가 있을 경우)
    for (ReceiveInfo info : receiveInfoList) { // List 수만큼 for문 반복
        int available = info.getAvailable_to_return();//List에 넘어온 데이터의 getAvailable_to_return()값을 available에 대입
%>
<tr>
    <td>
        <% if (available > 0) { %>
            <input type="radio" name="receiveId" value="<%= info.getReceive_id() %>"> <!-- 각 id마다 라디오버튼을 세팅 -->
        <% } else { %>
            <input type="radio" disabled> <!-- id가 없을 경우 세팅하지 않는데 이는 id가 있는곳에만 라디오 버튼이 생긴다는것 -->
        <% } %>
    </td>
    <td><%= info.getReceive_id() %></td>
    <td><%= info.getOrder_id() %></td>
    <td><%= info.getProduct_id() %></td>
    <td><%= info.getQuantity() %></td>
    <td><%= available %></td>
    <td><%= info.getReceive_date() != null ? sdf.format(info.getReceive_date()) : "" %></td> <!-- 날짜 형태로 나타내기 위한 변환 위의 sdf 참고 -->
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