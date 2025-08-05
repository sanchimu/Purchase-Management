<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>공급업체 목록</title>
<style>
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
<script>
    function confirmDelete(supplierId) {
        if (confirm("정말 삭제하시겠습니까?")) {
            location.href = "<c:url value='/deletesupplier.do'/>?id=" + supplierId;
        }
    }
</script>
</head>
<body>

<div class="top-menu">
    <a href="<c:url value='/insertsupplier.do'/>" class="btn">공급업체 등록</a>
</div>

<h2 style="text-align:center;">공급업체 목록</h2>

<table>
    <tr>
        <th>공급업체 ID</th>
        <th>공급업체 이름</th>
        <th>연락처</th>
        <th>주소</th>
        <th>삭제</th>
    </tr>
    
    <c:forEach var="supplier" items="${supplierList}">
        <tr>
            <td>${supplier.supplier_id}</td>
            <td>${supplier.supplier_name}</td>
            <td>${supplier.contact_number}</td>
            <td>${supplier.address}</td>
            <td>
                <form action="<c:url value='/deletesupplier.do'/>" method="post" style="display:inline;">
                    <input type="hidden" name="supplier_id" value="${supplier.supplier_id}">
                    <input type="submit" class="btn btn-delete" value="삭제"
                           onclick="return confirm('정말 삭제하시겠습니까?');">
                </form>
            </td>
        </tr>
    </c:forEach>

    <c:if test="${empty supplierList}">
        <tr>
            <td colspan="5">등록된 공급업체가 없습니다.</td>
        </tr>
    </c:if>
</table>

</body>
</html>
