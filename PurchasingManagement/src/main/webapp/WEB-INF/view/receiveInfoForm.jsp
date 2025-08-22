<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="ctx"  value="${pageContext.request.contextPath}"/>
<c:set var="edit" value="false"/>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>入庫登録</title>
<style>
  /* ページ全体の基本スタイル / 페이지 전체 기본 스타일 */
  body{font-family:"Noto Sans JP","Meiryo","Yu Gothic","Hiragino Kaku Gothic ProN",sans-serif;margin:20px;}
  h2{margin:0 0 12px;}
  .form{max-width:640px;padding:16px;border:1px solid #ddd;border-radius:8px;}
  .row{display:flex;align-items:center;margin:10px 0;gap:10px}
  .row label{width:150px;text-align:right;font-weight:600}
  select,input[type="number"],input[type="date"],input[type="text"]{flex:1;padding:8px 10px}
  .actions{margin-top:16px;display:flex;gap:8px}
  .btn{padding:8px 12px;border:1px solid #ccc;background:#f7f7f7;border-radius:6px;cursor:pointer}
  .err{color:#b00020;font-weight:bold;margin-bottom:8px}
</style>

<!-- 日付入力支援ライブラリ Flatpickr 読み込み / 날짜 입력 보조 라이브러리 Flatpickr 로드 -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
<script src="https://cdn.jsdelivr.net/npm/flatpickr/dist/l10n/ja.js"></script>
</head>
<body>
<h2>入庫登録</h2> <!-- 入庫登録画面タイトル / 입고 등록 화면 제목 -->

<!-- エラーメッセージがある場合表示 / 에러 메시지가 있으면 표시 -->
<c:if test="${not empty errorMsg}">
  <div class="err">${fn:escapeXml(errorMsg)}</div>
</c:if>

<!-- 入庫登録フォーム / 입고 등록 폼 -->
<form class="form" action="${ctx}/addReceiveInfo.do" method="post">
  <!-- 注文番号 選択 / 주문번호 선택 -->
  <div class="row">
    <label>注文番号</label>
    <select name="order_id" required>
      <option value="">-- 注文を選択 --</option>
      <c:forEach var="o" items="${orderList}">
        <option value="${o.order_id}" <c:if test="${not empty ri && ri.order_id == o.order_id}">selected</c:if>>
          ${o.order_id}
        </option>
      </c:forEach>
    </select>
  </div>

  <!-- 商品 選択 / 상품 선택 -->
  <div class="row">
    <label>商品</label>
    <select name="product_id" required>
      <option value="">-- 商品を選択 --</option>
      <c:forEach var="p" items="${productList}">
        <option value="${p.product_id}" <c:if test="${not empty ri && ri.product_id == p.product_id}">selected</c:if>>
          ${p.product_id} - ${p.product_name}
        </option>
      </c:forEach>
    </select>
  </div>

  <!-- 数量入力 (0以上必須) / 수량 입력 (0 이상 필수) -->
  <div class="row">
    <label>数量</label>
    <input type="number" name="quantity" min="0"
           value="<c:out value='${empty ri ? 0 : ri.quantity}'/>" required />
  </div>

  <!-- 入庫日入力 (未来禁止) / 입고일 입력 (미래 불가) -->
  <c:if test="${not empty ri && not empty ri.receive_date}">
    <fmt:formatDate value="${ri.receive_date}" pattern="yyyy-MM-dd" var="recvDate"/>
  </c:if>
  <jsp:useBean id="now" class="java.util.Date" />
  <fmt:formatDate value="${now}" pattern="yyyy-MM-dd" var="todayStr"/>
  <div class="row">
    <label>入庫日</label>
    <input type="text" id="receive_date" name="receive_date"
           value="${not empty recvDate ? recvDate : (not empty today ? today : todayStr)}" required />
  </div>

  <!-- 業務状態選択 (初期値=検収中) / 업무 상태 선택 (기본값=검수중) -->
  <div class="row">
    <label>業務状態</label>
    <select name="receive_status">
      <c:set var="sv" value="${empty ri || empty ri.receive_status ? '検収中' : ri.receive_status}"/>
      <option value="検収中"   <c:if test="${sv=='検収中'}">selected</c:if>>検収中</option>
      <option value="正常"     <c:if test="${sv=='正常'}">selected</c:if>>正常</option>
      <option value="入庫取消" <c:if test="${sv=='入庫取消'}">selected</c:if>>入庫取消</option>
      <option value="返品処理" <c:if test="${sv=='返品処理'}">selected</c:if>>返品処理</option>
    </select>
  </div>

  <!-- ボタンエリア / 버튼 영역 -->
  <div class="actions">
    <button type="submit" class="btn">登録</button> <!-- 登録ボタン / 등록 버튼 -->
    <a class="btn" href="${ctx}/listReceiveInfos.do">入庫リストに戻る</a> <!-- 入庫リストへ戻るリンク / 입고 리스트로 돌아가기 -->
  </div>
</form>

<!-- Flatpickr 初期化スクリプト / Flatpickr 초기화 스크립트 -->
<script>
document.addEventListener('DOMContentLoaded', function(){
  var el = document.getElementById('receive_date');
  if(!el) return;

  flatpickr.localize(flatpickr.l10ns.ja);
  flatpickr(el, {
    locale: 'ja',
    dateFormat: 'Y-m-d',   // サーバ送信形式 / 서버 전송 형식
    altInput: true,
    altFormat: 'Y年m月d日', // 画面表示形式 / 화면 표시 형식
    maxDate: 'today'       // 未来日禁止 / 미래일 금지
  });
});
</script>
</body>
</html>

