<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>購買リクエストリスト</title>
<style>
/* ★ 가로·세로 중앙 배치를 위한 기본 세팅 */
html, body {
	height: 100%;
}

body {
	font-family: Arial, sans-serif;
	margin: 0; /* 바깥 여백 제거 */
	background: #fff;
}

.viewport {
	min-height: 100vh;
	display: flex;
	align-items: flex-start; /* ← 세로 위쪽 정렬 */
	justify-content: center; /* ← 가로 중앙 */
	padding: 8px 0 16px; /* ← 상단 여백 확 줄임 */
}

/* 제목/툴바 기본 마진도 살짝 줄임 */
h2 {
	margin: 8px 0 12px;
	text-align: center;
	color: #333;
}

.toolbar {
	display: flex;
	gap: 12px;
	align-items: center;
	justify-content: center;
	margin: 6px 0 12px; /* ← 기존보다 작게 */
}

/* 검색 폼 */
.searchbar {
	display: flex;
	gap: 8px;
	align-items: center;
	justify-content: center;
	flex-wrap: wrap;
	margin-bottom: 10px; /* ← 살짝 축소 */
}

/* 테이블은 기존대로 래퍼 기준 중앙 */
table {
	border-collapse: collapse;
	width: 100%;
	max-width: 1100px;
	margin: 0 auto;
}

th, td {
	border: 1px solid #ddd;
	padding: 8px;
	text-align: center;
}

th {
	background: #f2f2f2;
}

.badge-ok {
	padding: 2px 8px;
	border-radius: 10px;
	background: #e6f6e6;
}

.badge-stop {
	padding: 2px 8px;
	border-radius: 10px;
	background: #fdeaea;
}

.muted {
	opacity: .55;
}

/* (옵션) 콘텐츠가 화면보다 훨씬 클 때엔 상단 정렬로 바꾸고 싶다면 아래 주석 해제
@media (max-height: 750px) {
  .viewport { align-items: flex-start; }
}
*/
a, a:visited {
	color: black;
	text-decoration: none;
}

a:hover {
	text-decoration: underline; /* (옵션) 마우스 올리면만 밑줄 */
}

.top-menu {
	margin: 10px 0;
	text-align: center;
}

.top-menu a {
	display: inline-block;
	padding: 8px 14px;
	margin-right: 6px;
	background-color: #4CAF50; /* 초록색 배경 */
	color: white; /* 흰 글씨 */
	text-decoration: none; /* 밑줄 제거 */
	border-radius: 4px; /* 둥근 모서리 */
	font-weight: bold;
}

.top-menu a:hover {
	background-color: #45a049; /* 마우스 올리면 조금 더 진한 초록 */
}
</style>
<script>
  function toggleAll(src){
    document.querySelectorAll('input[name="ids"]').forEach(cb => cb.checked = src.checked);
  }
  function getCheckedIds(){
    return Array.from(document.querySelectorAll('input[name="ids"]:checked')).map(cb=>cb.value);
  }
  function prepStatus(){
    const ids = getCheckedIds();
    if(!ids.length){alert('選択された項目がありません。'); return false;}
    const box = document.getElementById('idsContainer');
    box.innerHTML = '';
    ids.forEach(v=>{
      const h=document.createElement('input');
      h.type='hidden'; h.name='ids'; h.value=v;
      box.appendChild(h);
    });
    return true;
  }
  function validateSave(){
    const ids = getCheckedIds();
    if(!ids.length){alert('選択された項目がありません。'); return false;}
    return true;
  }
</script>
</head>
<body>

	<!-- ★ 전체 화면 중앙 래퍼 -->
	<div class="viewport">
		<!-- ★ 실제 페이지 콘텐츠 -->
		<div class="content">

			<h2>購買リクエストリスト</h2>

			<div class="top-menu">
			<a href="<c:url value='/insertpurchaserequest.do'/>">購買リクエスト登録</a>
			</div>


			<c:if test="${not empty message}">
				<div class="msg">${message}</div>
			</c:if>

			<!-- ★ 검색 폼에 중앙 정렬 클래스 적용 -->
			<form action="<c:url value='/searchpurchaserequest.do'/>"
				method="get" class="searchbar">
				リクエストID <input type="text" name="request_id"
					value="${param.request_id}"> 商品ID <input type="text"
					name="product_id" value="${param.product_id}"> 要請者 <input
					type="text" name="requester_name" value="${param.requester_name}">
				<input type="submit" value="探す"> <label
					style="margin-left: 10px;"> <input type="checkbox"
					onchange="location.href='?includeHidden='+(this.checked?'1':'0');"
					<c:if test="${param.includeHidden=='1' || includeHidden}">checked</c:if> />
					中断含め
				</label>
			</form>

			<form method="post">
				<input type="hidden" name="table" value="purchase_request" /> <input
					type="hidden" name="idColumn" value="request_id" />
				<div id="idsContainer"></div>

				<div class="toolbar">
					<button type="submit"
						formaction="${pageContext.request.contextPath}/updatePurchaseRequest.do"
						onclick="return prepStatus();">選択項目業務状態保存</button>

					<button type="submit"
						formaction="${pageContext.request.contextPath}/status/bulk.do"
						name="to" value="X"
						onclick="return prepStatus() && confirm('選択項目を中断しますか');">
						選択項目中断</button>

					<button type="submit"
						formaction="${pageContext.request.contextPath}/status/bulk.do"
						name="to" value="A" onclick="return prepStatus();">選択項目再開
					</button>
				</div>

				<!-- ✅ JSP 기본 리스트를 fn:split로 생성 (버전 호환) -->
				<c:if test="${empty requestStatusList}">
					<c:set var="requestStatusList"
						value="${fn:split('受付,検討中,承認,差し戻し,取り消し,完了', ',')}" />
				</c:if>

				<table>
					<tr>
						<th><input type="checkbox" onclick="toggleAll(this)"></th>
						<th>リクエストID</th>
						<th>商品ID</th>
						<th>サプライヤーID</th>
						<th>数量</th>
						<th>要請日</th>
						<th>要請者</th>
						<th>状態(業務)</th>
						<th>進行状況(A/X)</th>
					</tr>

					<c:if test="${empty requestList}">
						<tr>
							<td colspan="9">データがありません。</td>
						</tr>
					</c:if>

					<c:forEach var="req" items="${requestList}">
						<c:if
							test="${param.includeHidden=='1' || includeHidden || empty req.row_status || req.row_status=='A'}">
							<tr class="${req.row_status == 'X' ? 'muted' : ''}">
								<td><input type="checkbox" name="ids"
									value="${req.request_id}"></td>
								<td>${req.request_id}</td>
								<td>${req.product_id}</td>
								<td><c:out value="${req.supplier_id}" /></td>
								<td>${req.quantity}</td>
								<!-- 임시: -->
								<td><c:out value="${req.request_date}" /></td>
								<td>${req.requester_name}</td>
								<td><select name="${req.request_id}">
										<c:forEach var="st" items="${requestStatusList}">
											<option value="${st}"
												<c:if test="${st eq req.request_status}">selected</c:if>>${st}</option>
										</c:forEach>
								</select></td>
								<td><c:choose>
										<c:when test="${empty req.row_status || req.row_status=='A'}">
											<span class="badge-ok">進行中</span>
										</c:when>
										<c:otherwise>
											<span class="badge-stop">中断</span>
										</c:otherwise>
									</c:choose></td>
							</tr>
						</c:if>
					</c:forEach>
				</table>
			</form>

		</div>
		<!-- .content -->
	</div>
	<!-- .viewport -->

</body>
</html>
