<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>購買リクエスト 数量修正</title>
<style>
html, body {
	height: 100%;
}

body {
	font-family: Arial, sans-serif;
	margin: 0;
	background: #fff;
}

.viewport {
	min-height: 100vh;
	display: flex;
	align-items: flex-start; /* 위쪽 정렬 */
	justify-content: center; /* 가로 중앙 */
	padding: 8px 0 16px;
}

h2 {
	margin: 8px 0 12px;
	text-align: center;
	color: #333;
}

/* 폼 카드 */
.form-card {
	width: 100%;
	max-width: 600px;
	margin: 0 auto;
	border: 1px solid #ddd;
	border-radius: 6px;
	padding: 16px;
	background: #fff;
}

/* 필드 라인 */
.form-row {
	display: flex;
	align-items: center;
	gap: 12px;
	margin-bottom: 10px;
}

.form-row label {
	width: 140px;
	text-align: right;
	color: #333;
}

.form-row input[type="text"], .form-row input[type="number"] {
	flex: 1;
	padding: 8px;
	border: 1px solid #ddd;
	border-radius: 4px;
	font-size: 14px;
}

/* 액션 버튼: 리스트의 초록 버튼 톤과 맞춤 */
.actions {
	display: flex;
	gap: 10px;
	justify-content: center;
	margin-top: 12px;
}

.btn {
	display: inline-block;
	padding: 8px 14px;
	border-radius: 4px;
	text-decoration: none;
	cursor: pointer;
	font-weight: bold;
	border: 0;
}

.btn-primary {
	background-color: #4CAF50; /* 리스트 top-menu와 동일 톤 */
	color: #fff;
}

.btn-primary:hover {
	background-color: #45a049;
}

.btn-secondary {
	background: #f2f2f2;
	color: #333;
}

/* 보조 정보 박스 */
.meta {
	width: 100%;
	max-width: 600px;
	margin: 16px auto 0;
	color: #555;
}

.meta-row {
	margin-bottom: 6px;
}
</style>
</head>
<body>

	<div class="viewport">
		<div class="content" style="width: 100%; max-width: 1100px;">
			<h2>購買リクエスト 数量修正</h2>

			<form action="<c:url value='/purchaserequestqty.do'/>" method="post"
				class="form-card">
				<div class="form-row">
					<label>リクエストID</label> <input type="text" name="request_id"
						value="${req.request_id}" readonly>
				</div>
				<div class="form-row">
					<label>数量</label> <input type="number" name="quantity"
						value="${req.quantity}">
				</div>

				<div class="actions">
					<button type="submit" class="btn btn-primary">保存</button>
					<button type="button" class="btn btn-secondary"
						onclick="location.href='<c:url value="/requestList.do"/>'">一覧に戻る</button>
				</div>
			</form>

			<div class="meta">
				<div class="meta-row">商品ID: ${req.product_id}</div>
				<div class="meta-row">要請者: ${req.requester_name}</div>
				<div class="meta-row">
					要請日:
					<fmt:formatDate value="${req.request_date}" pattern="yyyy-MM-dd" />
				</div>
			</div>
		</div>
	</div>
	
	<c:if test="${not empty successMsg}">
		<script>
			alert("${successMsg}");
			location.href = "<c:url value='/requestList.do'/>";
		</script>
	</c:if>

	<c:if test="${empty successMsg and success}">
		<script>
			alert("数量を修正しました。");
			location.href = "<c:url value='/requestList.do'/>";
		</script>
	</c:if>

</body>
</html>