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
// 체크박스 선택 확인 (チェックボックス選択確認)
function validateForm() {
    const checkboxes = document.querySelectorAll('input[name="returnInfoIds"]:checked');
    if (checkboxes.length === 0) { // 체크된 체크박스 없을경우 (チェックされたチェックボックスがない場合)
        alert("キャンセルする返品リクエストを選択してください。");
        return false;
    }
    return true;
}
</script>
</head>
<body>

<!-- 상단 메뉴 버튼 추가 -->
<div class="top-menu">
    <a href="addReturnInfos.do" class="btn">商品を返品する</a>
    <a href="javascript:location.reload();" class="btn">再照会</a>
</div>

<h2 style="text-align:center;">返品リクエスト 照会</h2>


<!-- 검색 폼: 한 줄로 정리 -->
<form action="searchReturnInfo.do" method="get" style="width:80%; margin:20px auto; text-align:center;" onsubmit="return LastSearchResult();">	<!-- submit 시 실행된 action과 데이터 전달 방식 (get,post)세팅 -->
																																				<!-- submit時に実行されたactionとデータ伝達方式(get,post)セッティング -->
    返品番号 : <input type="text" name="return_id" value="${param.return_id}" style="margin-right:10px; padding:4px;">
    入庫番号 : <input type="text" name="receive_id" value="${param.receive_id}" style="margin-right:10px; padding:4px;">
    商品番号 : <input type="text" name="product_id" value="${param.product_id}" style="margin-right:10px; padding:4px;">
    <input type="submit" class="btn" value="照会する">
</form>

<h2 style="text-align:center;">返品要請リスト</h2>

<form action="deleteReturnInfos.do" method="post" style="width:80%; margin:auto;" onsubmit="return validateForm();"><!-- submit 시 실행된 action과 데이터 전달 방식 (get,post)세팅 -->
																													<!-- submit時に実行されたactionとデータ伝達方式(get,post)セッティング -->

    <table>
        <tr>
            <th>選択</th>
            <th>返品ID</th>
            <th>入庫ID</th>
            <th>商品名 (商品番号)</th>
            <th>返品数量</th>
            <th>返品事由</th>
            <th>返品日</th>
        </tr>
        <%
            List<ReturnInfo> returnInfoList = (List<ReturnInfo>) request.getAttribute("returnInfoList");  	//receiveInfoList 키에 저장된 값을 가져와서 List<ReceiveInfo>형으로 변환해서 receiveInfoList 객체에 저장
            																								//receiveInfoListキーに保存された値を取得し、List<ReceiveInfo>型に変換してreceiveInfoListオブジェクトに保存
            if (returnInfoList != null && !returnInfoList.isEmpty()) { //returnInfo 데이터가 있을 경우 (returnInfo データがある場合)
                for (ReturnInfo r : returnInfoList) { // 데이터 있는 수만큼 for문 반복 (データがある数だけfor文を繰り返す)
        %>
        <tr>
            <td><input type="checkbox" name="returnInfoIds" value="<%= r.getReturn_id() %>"></td>  <!-- 각 id마다 체크박스를 세팅 (IDごとにチェックボックスをセット) -->
            <td><%= r.getReturn_id() %></td>
            <td><%= r.getReceive_id() %></td>
            <td><%= r.getProduct_name() %> (<%= r.getProduct_id() %>)</td>
            <td><%= r.getQuantity() %></td>
            <td><%= r.getReason() %></td>
            <td><%= r.getReturn_date() %></td>
        </tr>
        <% } } else { %> <!-- returnInfo 데이터가 없을 경우 (returnInfo データがない場合) -->
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