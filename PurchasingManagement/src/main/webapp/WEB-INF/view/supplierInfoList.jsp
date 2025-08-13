<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>공급업체 목록</title>
<style> /* 디자인 지정 */
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
    .btn {
        padding: 4px 10px;
        background-color: #4CAF50;
        color: white;
        border: none;
        cursor: pointer;
        text-decoration: none;
    }
    .btn-delete {
        background-color: #f44336;
    }
    .btn:hover {
        opacity: 0.8;
    }
    .top-menu {
        width: 80%;
        margin: 20px auto;
        text-align: right;
    }
</style>
</head>
<body>

<div class="top-menu">
    <a href="<c:url value='/insertsupplier.do'/>" class="btn">공급업체 등록</a>
    <a href="<c:url value='/searchsupplier.do'/>" class="btn">공급업체 이름으로 검색</a>
    <a href="javascript:location.reload();" class="btn">새로고침</a>
</div>

<h2 style="text-align:center;">공급업체 목록</h2>

<form action="<c:url value='/deletesupplier.do'/>" method="post"
      onsubmit="return confirm('정말 선택한 항목을 삭제하시겠습니까?');">

<table>
    <tr>
        <th>선택</th>
        <th>공급업체 ID</th>
        <th>공급업체 이름</th>
        <th>연락처</th>
        <th>주소</th>
    </tr>
    
    <c:forEach var="supplier" items="${supplierList}">
        <tr>
            <td>
                <input type="checkbox" name="supplier_id" value="${supplier.supplier_id}">
            </td>
            <td>${supplier.supplier_id}</td>
            <td>${supplier.supplier_name}</td>
            <td>${supplier.contact_number}</td>
            <td>${supplier.address}</td>
        </tr>
    </c:forEach>

    <c:if test="${empty supplierList}">
        <tr>
            <td colspan="5">등록된 공급업체가 없습니다.</td>
        </tr>
    </c:if>
</table>

<div style="text-align:center; margin-top:10px;">
    <input type="submit" class="btn btn-delete" value="선택 삭제">
</div>

</form>

</body>
</html>
