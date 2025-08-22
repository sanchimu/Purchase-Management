<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.purchase.vo.ReturnInfo" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>返品要請リスト</title>
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
        alert("삭キャンセルする返品リクエストを選択してください。");
        return false;
    }
    return true;
}
</script>
</head>
<body>

<!-- 상단 메뉴 -->
<div class="top-menu">
    <a href="addReturnInfos.do" class="btn">商品を返品する</a>
    <a href="javascript:location.reload();" class="btn">再照会</a>
</div>

<h2 style="text-align:center;">返品リクエスト 照会</h2>


<!-- 검색 폼: 한 줄로 정리 -->
<form action="searchReturnInfo.do" method="get" style="width:80%; margin:20px auto; text-align:center;" onsubmit="return LastSearchResult();">
    返品番号 : <input type="text" name="return_id" value="${param.return_id}" style="margin-right:10px; padding:4px;">
    入庫番号 : <input type="text" name="receive_id" value="${param.receive_id}" style="margin-right:10px; padding:4px;">
    商品番号 : <input type="text" name="product_id" value="${param.product_id}" style="margin-right:10px; padding:4px;">
    <input type="submit" class="btn" value="照会する">
</form>

<h2 style="text-align:center;">返品要請リスト</h2>

<form action="deleteReturnInfos.do" method="post" style="width:80%; margin:auto;" onsubmit="return validateForm();">
    <%
        Boolean NoSearchReturnInfoResult = (Boolean) request.getAttribute("NoSearchReturnInfoResult");
        if (Boolean.TRUE.equals(NoSearchReturnInfoResult)) { %>
        <script>alert("照会された返品リクエストはありません。 完全なリストを表示します。");</script>
    <% } %>

    <table>
        <tr>
            <th>選択</th>
            <th>返品番号</th>
            <th>入庫番号</th>
            <th>商品名 (商品番号)</th>
            <th>返品数量</th>
            <th>返品事由</th>
            <th>返品日</th>
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
            <td colspan="7" style="text-align:center;">条件に合う返品リクエストはありません。</td>
        </tr>
        <% } %>
    </table>
    <br>
    <div style="width:80%; margin:0 auto; text-align:left;">
    	<input type="submit" class="btn" value="選択項目 返品キャンセル">
    </div>
</form>

</body>
</html>