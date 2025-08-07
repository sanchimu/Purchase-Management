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
<%-- 자바스크립트 코드 시작. 
자바스크립트코드 : 클라이언트쪽에서 실행되는 동작. JSP나 자바처럼 서버에서 실행되는게 아니라 사용자의
화면에서 즉시 동작. --%>
    function confirmDelete(supplierId) {
        if (confirm("정말 삭제하시겠습니까?")) {
        	<%-- confirm()은 확인창 표시 --%>
            location.href = "<c:url value='/deletesupplier.do'/>?id=" + supplierId;
            <%-- 사용자가 '확인'을 누르면 해당 URL로 이동 --%>
        }
    }
</script>
</head>
<body>

<div class="top-menu">
<!-- <div>...</div> : HTML에서 영역을 묶는 태그. 안에 있는 내용들을 하나의 블록으로 묶음 -->
    <a href="<c:url value='/insertsupplier.do'/>" class="btn">공급업체 등록</a>
    <%--공급업체 등록 페이지로 이동하는 버튼
     <c:url> : contextPath 자동 포함 (프로젝트 경로 자동 붙임)--%>
</div>

<h2 style="text-align:center;">공급업체 목록</h2>

<table> <!-- 표 시작  -->
    <tr> <!-- 표의 첫번째 행 -->
        <th>공급업체 ID</th>
        <th>공급업체 이름</th>
        <th>연락처</th>
        <th>주소</th>
        <th>삭제</th>
    </tr>
    
    <%--공급업체 목록을 테이블로 반복 출력하고 각 행마다 삭제 버튼 만듦 --%>
    <c:forEach var="supplier" items="${supplierList}">
    <!-- 반복 태그 역할. ${supplierList}는 서버에서 JSP로 넘겨준 List 객체
    var="supplier" 반복할 때마다 supplierList의 각 요소를 supplier 라는 이름으로 사용 -->
        <tr>
            <td>${supplier.supplier_id}</td> <!-- supplier객체의 supplier_id 필드 값 출력 -->
            <td>${supplier.supplier_name}</td>
            <td>${supplier.contact_number}</td>
            <td>${supplier.address}</td>
            <td> <!-- 공급업체 삭제 폼 -->
                <form action="<c:url value='/deletesupplier.do'/>" method="post" style="display:inline;">
                <!-- 삭제 요청을 보낼 폼. style="display:inline;" : 폼이 테이블에서 줄바꿈 없이 버튼만 보이게 함 -->
                    <input type="hidden" name="supplier_id" value="${supplier.supplier_id}">
                    <!-- 숨겨진 입력 필드. 사용자는 못보지만 폼 전송시 함께 전송
                    현재 행의 supplier_id 값 전송 -->
                    <input type="submit" class="btn btn-delete" value="삭제"
                           onclick="return confirm('정말 삭제하시겠습니까?');">
                           <!-- 버튼 클릭 시 자바스크립트 confirm창 띄움 -->
                </form>
            </td>
        </tr>
    </c:forEach>

    <c:if test="${empty supplierList}">
    <!-- supplierList가 비어있으면 -->
        <tr>
            <td colspan="5">등록된 공급업체가 없습니다.</td>
            <!-- 표 5칸 합쳐서 안내 문구 출력 -->
        </tr>
    </c:if>
</table>

</body>
</html>
