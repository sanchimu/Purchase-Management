<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>구매요청 목록</title>
</head>
<body>

<h2>구매요청 목록</h2>
<a href="<c:url value='/insertpurchaserequest.do'/>">구매요청 등록</a>
<table border="1">
    <tr>
        <th>요청ID</th>
        <th>상품ID</th>
        <th>수량</th>
        <th>요청일</th>
        <th>요청자</th>
        <th>삭제</th>
    </tr>
    <c:forEach var="req" items="${requestList}">
        <tr>
            <td>${req.request_id}</td>
            <td>${req.product_id}</td>
            <td>${req.quantity}</td>
            <td>${req.request_date}</td>
            <td>${req.requester_name}</td>
            <td>
                <form action="<c:url value='/deletepurchaserequest.do'/>" method="post">
                    <input type="hidden" name="request_id" value="${req.request_id}">
                    <input type="submit" value="삭제" onclick="return confirm('삭제하시겠습니까?');">
                </form>
            </td>
        </tr>
    </c:forEach>
</table>

</body>
</html>