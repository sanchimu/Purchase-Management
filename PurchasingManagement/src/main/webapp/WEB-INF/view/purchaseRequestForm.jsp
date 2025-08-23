<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>구매요청 등록</title>
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

input[type="text"], select, input[type="number"] {
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

.muted {
	color: #777;
	font-size: 0.95em;
	margin-top: 6px;
}

.disabled {
	opacity: .6;
	pointer-events: none;
}
</style>
</head>
<body>

	<h2>購買要請登録</h2>

	<div class="toolbar">
		<a class="btn" href="<c:url value='/requestList.do'/>">リストに戻る</a>
	</div>

	<!-- 메세지 값이 있으면 표시
	メッセージがある場合は表示 -->
	<c:if test="${not empty message}">
		<div class="msg">${message}</div>
	</c:if>

		<!-- 상품이 없을 때 안내
		商品リストが空いてる場合 -->
	<c:choose>
		<c:when test="${empty productList}">
			<div class="msg">商品が登録されていません。先に商品をご登録ください。</div>
			<p>
				<a class="btn" href="<c:url value='/product/new.do'/>">商品登録</a> <a
					class="btn" href="<c:url value='/requestList.do'/>">リスト</a>
			</p>
		</c:when>

		<%-- 상품이 있을 때 폼 표시
		それ以外の場合（品がある場合）--%>
		<c:otherwise>
			<form action="<c:url value='/insertpurchaserequest.do'/>"
				method="post">
				<p>
					<label for="product_id">商品選択</label><br> <select
						id="product_id" name="product_id" required>
						<option value="">-- 商品を選んでください --</option>
						
						<!-- productList를 반복하여 각 상품을 표시
						productList をループして商品を表示 -->
						<!-- 선택된 상품이면 'selected' 로 변경
						選択された商品は’selected’ に変更-->
						<c:forEach var="p" items="${productList}">
							<option value="${p.product_id}"
								<c:if test="${p.product_id == selectedProductId}">selected</c:if>>
								${p.product_name}
								<c:if test="${not empty p.category}"> / ${p.category}</c:if>
								<c:if test="${not empty p.price}"> / ₩${p.price}</c:if> (ID:
								${p.product_id})
							</option>
						</c:forEach>
					</select>
				<div class="muted">※ より正確にお選びいただくために、商品名・カテゴリ・価格をご確認ください。</div>
				</p>

				<!-- 수량 입력 필드 (기본값 1, 최소 1) // 数量入力フィールド (デフォルト1, 最小1) -->
				<p>
					<label for="quantity">数量</label><br> <input type="number"
						id="quantity" name="quantity" placeholder="1" min="1" step="1"
						required value="<c:out value='${param.quantity}'/>">
				</p>

				<p>
					<label for="requester_name">要請者</label><br> <input type="text"
						id="requester_name" name="requester_name" maxlength="50" required
						value="<c:out value='${param.requester_name}'/>">
				</p>

				<c:if test="${not empty formError}">
					<div class="msg">${formError}</div>
				</c:if>

				<!-- 등록 후 목록 페이지로 이동
				登録後、リストページに移動-->
				<p>
					<input type="submit" value="登録"> <a class="btn"
						href="<c:url value='/requestList.do'/>">リストに戻る</a>
				</p>
			</form>
		</c:otherwise>
	</c:choose>

</body>
</html>
