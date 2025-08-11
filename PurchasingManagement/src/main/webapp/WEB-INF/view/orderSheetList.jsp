<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>발주서 목록</title>
</head>
<body>

	<h2>발주서 목록</h2>

    <table border="1" cellpadding="10">
        <tr>
            <th>발주 ID</th>
            <th>구매요청 ID</th>
            <th>공급업체 ID</th>
            <th>발주일</th>
            <th>상태</th>
        </tr>
        <c:forEach var="order" items="${orderList}">
            <tr>
                <td>${order.order_id}</td>
                <td>${order.request_id}</td>
                <td>${order.supplier_id}</td>
                <td><fmt:formatDate value="${order.order_date}" pattern="yyyy-MM-dd"/></td>
                <td>${order.order_status}</td>
            </tr>
        </c:forEach>
    </table>

    <br>
    <a href="addOrderSheet.do">▶ 발주서 등록하기</a>

</body>
</html>