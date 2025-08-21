<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>仕入先一覧</title>
<style>
/* テーブルデザイン設定 */
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

/* ボタンデザイン */
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

/* レイアウト用 */
.top-menu {
	width: 80%;
	margin: 20px auto;
	text-align: right;
}

.toolbar {
	width: 80%;
	margin: 10px auto;
	text-align: left;
}

/* ステータス表示用バッジ */
.badge-ok {
	padding: 2px 8px;
	border-radius: 10px;
	background: #e6f6e6
}

.badge-stop {
	padding: 2px 8px;
	border-radius: 10px;
	background: #fdeaea
}

.muted {
	opacity: .55
}
</style>
<script>
  // 全選択・全解除チェックボックス制御
  function toggleAll(src){
    document.querySelectorAll('input[name="supplier_id"]').forEach(cb => cb.checked = src.checked);
  }

  // 選択されたIDを配列で取得
  function getCheckedIds(){
    return Array.from(document.querySelectorAll('input[name="supplier_id"]:checked')).map(cb => cb.value);
  }

  // 状態(A/X)変更のために hidden input を生成
  function prepStatus(){
    const ids = getCheckedIds();
    if(ids.length === 0){ alert('対象を選択してください。'); return false; }
    const box = document.getElementById('idsContainer');
    box.innerHTML = '';
    ids.forEach(v => {
      const h = document.createElement('input');
      h.type = 'hidden'; h.name = 'ids'; h.value = v;
      box.appendChild(h);
    });
    return true;
  }

  // 業務状態保存前のバリデーション（選択されていない場合は警告）
  function validateSave(){
    const ids = getCheckedIds();
    if(ids.length === 0){ alert('対象を選択してください。'); return false; }
    return true;
  }
</script>
</head>
<body>

	<!-- 上部メニューバー: 新規登録 / 検索 / 再読み込み -->
	<div class="top-menu">
		<a href="<c:url value='/insertsupplier.do'/>" class="btn">仕入先登録</a> <a
			href="<c:url value='/searchsupplier.do'/>" class="btn">仕入先名で検索</a> <a
			href="javascript:location.reload();" class="btn">再読み込み</a>
	</div>

	<h2 style="text-align: center;">仕入先一覧</h2>

	<!-- 一つのフォームで「業務状態保存」と「表示状態(A/X)切り替え」をまとめて行う -->
	<form method="post">
		<!-- A/X 一括変更時に必要な共通パラメータ -->
		<input type="hidden" name="table" value="supplier_info" /> <input
			type="hidden" name="idColumn" value="supplier_id" />
		<div id="idsContainer"></div>
		<!-- JavaScriptで選択IDをhiddenに格納 -->

		<div class="toolbar">
			<!--  選択項目の業務状態を保存 -->
			<button class="btn"
				formaction="${pageContext.request.contextPath}/updatesupplier.do"
				onclick="return validateSave();">選択項目 業務状態保存</button>

			<!-- 表示状態(A/X)を一括変更: 中断(X)にする -->
			<button class="btn"
				formaction="${pageContext.request.contextPath}/status/bulk.do"
				name="to" value="X"
				onclick="return prepStatus() && confirm('選択項目を中断しますか？');">選択項目
				中断</button>

			<!-- 表示状態(A/X)を一括変更: 再開(A)にする -->
			<button class="btn"
				formaction="${pageContext.request.contextPath}/status/bulk.do"
				name="to" value="A" onclick="return prepStatus();">選択項目 再開</button>

			<!-- チェックを入れると「中断(X)」も表示対象に含める -->
			<label style="margin-left: 12px;"> <input type="checkbox"
				onchange="location.href='?includeHidden='+(this.checked?'1':'0');"
				<c:if test="${param.includeHidden=='1' || includeHidden}">checked</c:if> />
				中断を含む
			</label>
		</div>

		<!-- 状態ドロップダウンの基本リスト（ハンドラーから渡されない場合はここで生成） -->
		<c:if test="${empty supplierStatusList}">
			<c:set var="supplierStatusList"
				value="${fn:split('有効,取引停止,廃業,休眠', ',')}" />
		</c:if>

		<!-- 仕入先一覧テーブル -->
		<table>
			<tr>
				<th><input type="checkbox" onclick="toggleAll(this)"></th>
				<!-- 全選択 -->
				<th>仕入先ID</th>
				<th>仕入先名</th>
				<th>連絡先</th>
				<th>住所</th>
				<th>状態(業務)</th>
				<th>表示状態(A/X)</th>
			</tr>

			<!-- supplierList をループして一覧を出力 -->
			<c:forEach var="supplier" items="${supplierList}">
				<!-- デフォルトはAのみ表示。「中断を含む」が有効ならXも表示 -->
				<c:if
					test="${param.includeHidden=='1' || includeHidden || empty supplier.row_status || supplier.row_status=='A'}">
					<tr class="<c:if test='${supplier.row_status=="X"}'>muted</c:if>">
						<!-- 各行にチェックボックス（業務状態保存や表示状態変更で使用） -->
						<td><input type="checkbox" name="supplier_id"
							value="${supplier.supplier_id}"></td>
						<td><a
							href="<c:url value='/editsupplier.do'>
              <c:param name='supplier_id' value='${supplier.supplier_id}'/>
           </c:url>">
								${supplier.supplier_name} </a></td>
						<td>${supplier.supplier_name}</td>
						<td>${supplier.contact_number}</td>
						<td>${supplier.address}</td>

						<!-- 業務状態を変更するドロップダウン -->
						<td><select name="${supplier.supplier_id}">
								<c:forEach var="st" items="${supplierStatusList}">
									<option value="${st}"
										<c:if test="${st eq supplier.supplier_status}">selected</c:if>>${st}</option>
								</c:forEach>
						</select></td>

						<!-- 表示状態バッジ: Aなら「進行中」、Xなら「中断」 -->
						<td><c:choose>
								<c:when
									test="${empty supplier.row_status || supplier.row_status=='A'}">
									<span class="badge-ok">進行中</span>
								</c:when>
								<c:otherwise>
									<span class="badge-stop">中断</span>
								</c:otherwise>
							</c:choose></td>
					</tr>
				</c:if>
			</c:forEach>

			<!-- supplierList が空なら「登録された仕入先はありません」と表示 -->
			<c:if test="${empty supplierList}">
				<tr>
					<td colspan="7">登録された仕入先はありません。</td>
				</tr>
			</c:if>
		</table>
	</form>

</body>
</html>
