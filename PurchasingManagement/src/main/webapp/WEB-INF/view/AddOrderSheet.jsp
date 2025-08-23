<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>発注書登録</title>
<style>
  /* 画面全体のデザイン / 화면 전체 디자인 */
  body{font-family:Arial,Helvetica,sans-serif;margin:20px;}
  .row{margin:10px 0;}
  label{display:inline-block;width:130px}
  select,input{padding:6px 8px}
  .btn{padding:6px 10px;border:1px solid #ccc;background:#f7f7f7;cursor:pointer;text-decoration:none;color:#333}
  .msg{margin:8px 0;padding:8px 10px;border:1px solid #eedc82;background:#fff8d6}
</style>
<script>
  // 요청ID 선택 시 공급업체ID 자동 채움 / 申請ID選択時に供給業者IDを自動入力
  function onReqChange(sel){
    const opt = sel.options[sel.selectedIndex];
    document.getElementById('supplier_id').value = opt.getAttribute('data-supplier') || '';
  }
</script>
</head>
<body>

<!-- 
     発注書登録フォーム / 발주서 등록 폼
     - requestOptions : 구매요청 목록
     - orderStatusList: 발주서 상태 선택
    -->
<h2>発注書登録</h2>

<!-- エラーメッセージ表示 / 오류 메시지 출력 -->
<c:if test="${not empty error}">
  <div class="msg">${error}</div>
</c:if>

<form method="post" action="<c:url value='/addOrderSheet.do'/>">
  <!-- 요청ID 드롭다운 / 申請IDドロップダウン -->
  <div class="row">
    <label>申請ID</label>
    <select name="request_id" id="request_id" onchange="onReqChange(this)" required>
      <option value="">選択</option>
      <c:forEach var="o" items="${requestOptions}">
        <option value="${o.request_id}" data-supplier="${o.supplier_id}">
          ${o.request_id} / ${o.product_id} - ${o.product_name} 
          (申請者:${o.requester_name}, 数量:${o.request_quantity})
        </option>
      </c:forEach>
    </select>
  </div>

  <!-- 공급업체 ID 자동 채움 / 供給業者ID自動入力 -->
  <div class="row">
    <label>供給業者ID</label>
    <input type="text" id="supplier_id" value="" readonly />
    <span style="color:#999;font-size:12px">* 自動入力（サーバ側でも検証）</span>
  </div>

  <!-- 상태 선택 / 状態選択 -->
  <div class="row">
    <label>業務状態</label>
    <select name="order_status">
      <c:forEach var="st" items="${orderStatusList}">
        <option value="${st}">${st}</option>
      </c:forEach>
    </select>
  </div>

  <!-- 버튼 그룹 / ボタングループ -->
  <div class="row">
    <button type="submit" class="btn">登録</button>
    <a class="btn" href="<c:url value='/orderSheetList.do'/>">一覧</a>
  </div>
</form>

</body>
</html>
