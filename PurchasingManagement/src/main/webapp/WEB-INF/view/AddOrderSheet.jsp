<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>발주서 등록</title>
</head>
<body>

	<h2>발주서 등록</h2>

    <form action="addOrderSheet.do" method="post">
        <table border="1" cellpadding="10">
            <tr>
                <td>구매요청 ID (request_id)</td>
                <td><input type="text" name="request_id" required></td>
            </tr>
            <tr>
                <td>공급업체 ID (supplier_id)</td>
                <td><input type="text" name="supplier_id" required></td>
            </tr>
            <tr>
                <td>발주 상태 (order_status)</td>
                <td>
                    <select name="order_status">
                        <option value="요청">요청</option>
                        <option value="완료">완료</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <input type="submit" value="등록">
                </td>
            </tr>
        </table>
    </form>

    <c:if test="${not empty error}">
        <p style="color:red;">${error}</p>
    </c:if>

    <br>
    <a href="orderSheetList.do">▶ 발주서 목록으로 이동</a>

</body>
</html>