<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.purchase.vo.ReturnInfo" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>반품 정보 목록</title>
<style>
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

/* 버튼 스타일 */
.btn {
	padding: 4px 10px;
	background-color: #4CAF50;
	color: white;
	border: none;
	cursor: pointer;
	text-decoration: none;
}

.btn:hover {
	opacity: 0.8;
}

/* 상단 메뉴 레이아웃 */
.top-menu {
	width: 80%;
	margin: 20px auto;
	text-align: right;
}

/* 회색 처리 (예: 취소된 반품 표시용, 필요 시) */
.muted {
	opacity: .55;
}
</style>
<script>
// 체크박스 선택 확인
function validateForm() {
    const checkboxes = document.querySelectorAll('input[name="returnInfoIds"]:checked');
    if (checkboxes.length === 0) {
        alert("삭제할 반품 정보를 선택하세요.");
        return false;
    }
    return true;
}
</script>
</head>
<body>

<!-- 상단 메뉴 -->
<div class="top-menu">
    <a href="addReturnInfos.do" class="btn">상품 반품하기</a>
    <a href="javascript:location.reload();" class="btn">재조회</a>
</div>

<h2 style="text-align:center;">반품 정보 조회</h2>


<!-- 검색 폼: 한 줄로 정리 -->
<form action="searchReturnInfo.do" method="get" style="width:80%; margin:20px auto; text-align:center;" onsubmit="return LastSearchResult();">
    반품 고유 번호 : <input type="text" name="return_id" value="${param.return_id}" style="margin-right:10px; padding:4px;">
    입고 고유 번호 : <input type="text" name="receive_id" value="${param.receive_id}" style="margin-right:10px; padding:4px;">
    상품 고유 번호 : <input type="text" name="product_id" value="${param.product_id}" style="margin-right:10px; padding:4px;">
    <input type="submit" class="btn" value="조회">
</form>

<h2 style="text-align:center;">반품 정보 목록</h2>

<form action="deleteReturnInfos.do" method="post" style="width:80%; margin:auto;" onsubmit="return validateForm();">
    <%
        Boolean NoSearchReturnInfoResult = (Boolean) request.getAttribute("NoSearchReturnInfoResult");
        if (Boolean.TRUE.equals(NoSearchReturnInfoResult)) { %>
        <script>alert("조회된 반품 요청 사항이 없습니다. 전체 목록을 표시합니다.");</script>
    <% } %>

    <table>
        <tr>
            <th>선택</th>
            <th>반품 고유 번호</th>
            <th>입고 고유 번호</th>
            <th>상품명 (상품 번호)</th>
            <th>반품 수량</th>
            <th>반품 사유</th>
            <th>반품 날짜</th>
        </tr>
        <%
            List<ReturnInfo> returnInfoList = (List<ReturnInfo>) request.getAttribute("returnInfoList");
            if (returnInfoList != null && !returnInfoList.isEmpty()) {
                for (ReturnInfo r : returnInfoList) {
        %>
        <tr>
            <td><input type="checkbox" name="returnInfoIds" value="<%= r.getReturn_id() %>"></td>
            <td><%= r.getReturn_id() %></td>
            <td><%= r.getReceive_id() %></td>
            <td><%= r.getProduct_name() %> (<%= r.getProduct_id() %>)</td>
            <td><%= r.getQuantity() %></td>
            <td><%= r.getReason() %></td>
            <td><%= r.getReturn_date() %></td>
        </tr>
        <% } } else { %>
        <tr>
            <td colspan="7" style="text-align:center;">조회된 반품 요청 사항이 없습니다.</td>
        </tr>
        <% } %>
    </table>
    <br>
    <input type="submit" class="btn" value="선택 항목 반품 취소">
</form>

</body>
</html>