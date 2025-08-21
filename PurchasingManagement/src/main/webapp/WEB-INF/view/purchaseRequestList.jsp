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

/* ページのデザイン */
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
	align-items: flex-start;
	justify-content: center;
	padding: 8px 0 16px;
}

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
	margin: 6px 0 12px;
}

/* 検索 フォーム */
.searchbar {
	display: flex;
	gap: 8px;
	align-items: center;
	justify-content: center;
	flex-wrap: wrap;
	margin-bottom: 10px;
}

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

a, a:visited {
	color: black;
	text-decoration: none;
}

a:hover {
	text-decoration: underline;
}

.top-menu {
	margin: 10px 0;
	text-align: center;
}

.top-menu a {
	display: inline-block;
	padding: 8px 14px;
	margin-right: 6px;
	background-color: #4CAF50;
	color: white;
	text-decoration: none;
	border-radius: 4px;
	font-weight: bold;
}

.top-menu a:hover {
	background-color: #45a049;
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

	<div class="viewport">
		<div class="content">
			<h2>購買リクエストリスト</h2>

			<!-- 구매요청 등록 페이지로 이동하는 버튼
			購買リクエスト登録ページに移動するボタン -->
			<div class="top-menu">
				<a href="<c:url value='/insertpurchaserequest.do'/>">購買リクエスト登録</a>
			</div>

			<c:if test="${not empty message}">
				<div class="msg">${message}</div>
			</c:if>

			<!-- 요청ID, 상품ID, 요청자로 검색하는 기능
			リクエストID、商品ID、要請者で検索する機能 -->
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

				<!-- 서버에서 상태 목록을 받지 않았다면 기본값 생성
				サーバーからステータスリストを受けないとデフォルトを入れる -->
				<c:if test="${empty requestStatusList}">
					<c:set var="requestStatusList"
						value="${fn:split('受付,検討中,承認,差し戻し,取り消し,完了', ',')}" />
				</c:if>

				<table>
					<tr>
						<!-- 전체 선택/해제 체크박스
						全体項目を選択・解除するチェックボックス-->
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

					<!-- 데이터가 없을 때만 메세지 출력
					データがない場合のみ出るメッセージ -->
					<c:if test="${empty requestList}">
						<tr>
							<td colspan="9">データがありません。</td>
						</tr>
					</c:if>

					<!-- 리스트의 각 항목을 req로 바인딩하여 반복
					リストの各項目をreqにバインディングして繰り返す -->
					<c:forEach var="req" items="${requestList}">
						<!-- 진행 상태가 'X'인 행은 기본적으로 숨기고 체크박스가 켜져있거나 진행 상태가 'A'라면 표시
						進行状態が「X」の行は基本的に非表示にし、チェックボックスがオンになっているか、進行状態が「A」であれば表示 -->
						<c:if
							test="${param.includeHidden=='1' || includeHidden || empty req.row_status || req.row_status=='A'}">

							<!-- 진행 상태가 'X'라면 흐리게 표시
							進行状態が'X'ならぼかし表示 -->
							<tr class="${req.row_status == 'X' ? 'muted' : ''}">

								<!-- 행 선택 체크박스
								行選択チェックボックス -->
								<td><input type="checkbox" name="ids"
									value="${req.request_id}"></td>
								<td>${req.request_id}</td>
								<td>${req.product_id}</td>
								<td><c:out value="${req.supplier_id}" /></td>

								<!-- 수량은 파란 링크로 표시, 클릭 시 수정 폼으로 이동
								数量は青いリンクで表示、クリックすると修正フォームに移動 -->
								<td><a
									href="<c:url value='/purchaserequestqty.do'/>?request_id=${req.request_id}"
									style="color: blue;"> ${req.quantity} </a></td>
								<td><c:out value="${req.request_date}" /></td>
								<td>${req.requester_name}</td>

								<!--  드롭다운 (요청 ID를 이름으로 넣어서 구분)
								ドロップダウン(リクエストIDを名前で入れて区分) -->
								<td><select name="${req.request_id}">
										<c:forEach var="st" items="${requestStatusList}">
											<option value="${st}"
												<c:if test="${st eq req.request_status}">selected</c:if>>${st}</option>
										</c:forEach>
								</select></td>
								<td><c:choose>
										<c:when test="${req.row_status=='A'}">
											<!--  진행중(A) 이라면 진행중 표시
											進行中(A)なら'進行中'表示 -->
											<span class="badge-ok">進行中</span>
										</c:when>
										<c:otherwise>
											<!--  그 외는 중단 표시 
											その他は'中断'表示-->
											<span class="badge-stop">中断</span>
										</c:otherwise>
									</c:choose></td>
							</tr>
						</c:if>
					</c:forEach>
				</table>
			</form>
		</div>
	</div>
</body>
</html>
