<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>구매요청 검색</title>
<style>
body {
	font-family: Arial, sans-serif;
	margin: 20px;
}

h2 {
	color: #333;
}

.toolbar {
	display: flex;
	gap: 12px;
	align-items: center;
	margin: 10px 0 16px;
}

.toolbar form {
	display: flex;
	gap: 8px;
	align-items: center;
}

input[type="text"] {
	padding: 6px 8px;
}

input[type="submit"], a.btn {
	padding: 6px 10px;
	border: 1px solid #ccc;
	background: #f7f7f7;
	text-decoration: none;
	color: #333;
	cursor: pointer;
}

.msg {
	margin: 8px 0;
	padding: 8px 10px;
	background: #fff8d6;
	border: 1px solid #eedc82;
}

table {
	border-collapse: collapse;
	width: 100%;
	max-width: 900px;
}

th, td {
	border: 1px solid #ddd;
	padding: 8px;
	text-align: center;
}

th {
	background: #f2f2f2;
}
</style>
</head>
<body>

	<h2>구매요청 검색 (상품ID)</h2>

	<div class="toolbar">

		<!-- POST 방식으로 서버에 정보 전송
	POST方式でサーバーに情報送信 -->
		<form action="<c:url value='/searchpurchaserequest.do'/>"
			method="post">
			<label for="product_id">상품ID</label> <input type="text"
				id="product_id" name="product_id" placeholder="P001"
				pattern="^P[0-9]{3}$" title="형식은 P001 (P + 3자리 숫자)"
				value="${product_id}" /> <input type="submit" value="검색" /> <a
				class="btn" href="<c:url value='/searchpurchaserequest.do'/>">초기화</a>
		</form>

		<a class="btn" href="<c:url value='/insertpurchaserequest.do'/>">구매요청
			등록</a> <a class="btn" href="<c:url value='/requestList.do'/>">목록으로</a>
	</div>
	<c:if test="${not empty message}">
		<div class="msg">${message}</div>
	</c:if>

	<!-- 검색 결과(requestList)가 있을 경우 테이블 표시
検索結果(requestList)がある場合テーブルを表示 -->
	<c:if test="${not empty requestList}">
		<table>
			<tr>
				<th>요청ID</th>
				<th>상품ID</th>
				<th>수량</th>
				<th>요청일</th>
				<th>요청자</th>
				<th>삭제</th>
			</tr>

			<!-- requestList를 반복하여 각 요청 데이터를 표시
			requestList をループして各依頼データを表示 -->
			<c:forEach var="req" items="${requestList}">
				<tr>
					<td>${req.request_id}</td>
					<td>${req.product_id}</td>
					<td>${req.quantity}</td>
					<td>${req.request_date}</td>
					<td>${req.requester_name}</td>
					<td>
						<!-- 삭제 요청 폼 
						削除リクエストフォーム-->
						<form action="<c:url value='/deletepurchaserequest.do'/>"
							method="post" style="margin: 0">
							<input type="hidden" name="request_id" value="${req.request_id}" />
							<input type="submit" value="삭제"
								onclick="return confirm('삭제하시겠습니까?');" />
						</form>
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>

</body>
</html>
