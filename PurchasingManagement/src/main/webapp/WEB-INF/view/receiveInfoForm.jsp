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
  /* 화면 전반 기본 스타일 / ページ共通の軽いスタイル */
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

<!-- 날짜 피커(flatpickr) 로드 / Flatpickr 読み込み -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
<script src="https://cdn.jsdelivr.net/npm/flatpickr/dist/l10n/ja.js"></script>
</head>
<body>
<h2>入庫登録</h2> <!-- 화면 제목 / 画面タイトル -->

<!-- 에러 메시지 있으면 보여주기 / エラーがあれば表示 -->
<c:if test="${not empty errorMsg}">
  <div class="err">${fn:escapeXml(errorMsg)}</div>
</c:if>

<!-- 입고 등록 폼 / 入庫登録フォーム -->
<form class="form" action="${ctx}/addReceiveInfo.do" method="post">
  <!-- 주문번호 선택 / 注文番号の選択 -->
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

  <!-- 상품 선택 / 商品の選択 -->
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

  <!-- 수량 입력(0 이상 필수) / 数量入力(0以上必須) -->
  <div class="row">
    <label>数量</label>
    <input type="number" name="quantity" min="0"
           value="<c:out value='${empty ri ? 0 : ri.quantity}'/>" required />
  </div>

  <!-- 입고일 입력(미래 불가) / 入庫日入力(未来不可) -->
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

  <!-- 업무 상태(기본=受入済) / 業務状態(初期=受入済) -->
  <div class="row">
    <label>業務状態</label>
    <select name="receive_status">
      <c:set var="sv" value="${empty ri || empty ri.receive_status ? '受入済' : ri.receive_status}"/>
      <option value="受入済"   <c:if test="${sv=='受入済'}">selected</c:if>>受入済</option>
      <option value="正常"     <c:if test="${sv=='正常'}">selected</c:if>>正常</option>
      <option value="入荷取消" <c:if test="${sv=='入荷取消'}">selected</c:if>>入荷取消</option>
      <option value="返品処理" <c:if test="${sv=='返品処理'}">selected</c:if>>返品処理</option>
    </select>
  </div>

  <!-- 버튼 영역 / ボタンエリア -->
  <div class="actions">
    <button type="submit" class="btn">登録</button> <!-- 등록 -->
    <a class="btn" href="${ctx}/listReceiveInfos.do">入庫リストに戻る</a> <!-- 목록으로 -->
  </div>
</form>

<!-- 날짜 피커 초기화 / Flatpickr 初期化 -->
<script>
document.addEventListener('DOMContentLoaded', function(){
  var el = document.getElementById('receive_date');
  if(!el) return;

  flatpickr.localize(flatpickr.l10ns.ja);
  flatpickr(el, {
    locale: 'ja',
    dateFormat: 'Y-m-d',   // 서버로 보낼 값의 형식 / サーバ送信用フォーマット
    altInput: true,        // 사용자에겐 보기 좋은 표시용 입력 생성 / 表示用の別入力を出す
    altFormat: 'Y年m月d日', // 화면 표시 형식 / 画面表示フォーマット
    maxDate: 'today'       // 미래 선택 막기 / 未来日を選べないように
  });
});
</script>
</body>
</html>
