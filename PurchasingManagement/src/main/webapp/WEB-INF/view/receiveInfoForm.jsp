<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> <%-- JSP 기본 설정 (일본語: JSP基本設定 / 한국어: JSP 페이지 기본 설정) --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>  <%-- JSTL 코어 태그 (条件分岐/繰返しなど) / JSTL 기본 태그 (if, forEach 등) --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%> <%-- JSTL 포맷 태그 (日付/数値フォーマット) / 날짜·숫자 포맷 처리 --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> <%-- JSTL 함수 태그 (文字列処理など) / 문자열 함수 제공 --%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/> <%-- コンテキストパス設定 / 컨텍스트 경로 설정 --%>
<c:set var="edit" value="${not empty ri}"/> <%-- 修正モードか新規登録か判定 / 수정 모드인지 신규 등록인지 확인 --%>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>${edit ? '入庫修正' : '入庫登録'}</title> <%-- ページタイトル (入庫修正 or 入庫登録) / 페이지 제목 (입고 수정/등록) --%>
<style>
  /* フォームのスタイル / 폼 스타일 정의 */
  body { font-family: Arial, sans-serif; margin: 20px; }
  .form { max-width: 640px; padding: 16px; border: 1px solid #ddd; border-radius: 6px; }
  .row { display:flex; align-items:center; margin:10px 0; gap:10px; }
  .row label { width: 140px; text-align: right; font-weight:600; }
  input[type="text"], input[type="number"], input[type="date"], input[type="text"].datepicker, select { flex:1; padding:8px 10px; }
  .actions { margin-top: 16px; display:flex; gap:10px; }
  .btn { padding:8px 12px; border:1px solid #ccc; background:#f7f7f7; text-decoration:none; color:#333; cursor:pointer; }
  .hint { color:#666; font-size:12px; margin-left:140px; }
</style>

<!-- flatpickr (カレンダーライブラリ) / 캘린더 라이브러리 -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
<script src="https://cdn.jsdelivr.net/npm/flatpickr/dist/l10n/ja.js"></script>

<script>
  // ページ読み込み後、日付入力欄にカレンダーを適用
  // 페이지 로드 후 날짜 입력창에 캘린더 적용
  document.addEventListener("DOMContentLoaded", function() {
    flatpickr("#receive_date", {
      locale: "ja",
      dateFormat: "Y年m月d日"
    });
  });
</script>
</head>
<body>

<h2>${edit ? '入庫修正' : '入庫登録'}</h2> <%-- 제목 표시 (수정/등록) --%>

<form class="form" action="<c:url value='/addReceiveInfo.do'/>" method="post"> <%-- フォーム送信先 / 폼 전송 경로 --%>
  <c:if test="${edit}">
    <input type="hidden" name="receive_id" value="${ri.receive_id}"/> <%-- 修正時のみID保持 / 수정 시 기존 ID 유지 --%>
  </c:if>
  
  <c:if test="${not empty errorMsg}">
    <div style="color:#b00020; font-weight:bold; margin-bottom:10px;">
      ${errorMsg} <%-- エラーメッセージ表示 / 에러 메시지 표시 --%>
    </div>
  </c:if>

  <!-- 注文ID (注文選択) / 주문ID (주문 선택) -->
  <div class="row">
    <label>注文ID</label>
    <select name="order_id" required>
      <option value="">-- 注文を選択してください --</option> <%-- 선택 안내 --%>
      <c:forEach var="o" items="${orderList}">
        <option value="${o.request_id}" 
          <c:if test="${edit and o.request_id == ri.order_id}">selected</c:if>>
          ${o.request_id}
        </option>
      </c:forEach>
    </select>
  </div>

  <!-- 商品ID (商品選択) / 상품ID (상품 선택) -->
  <div class="row">
    <label>商品ID</label>
    <select name="product_id" required>
      <option value="">-- 商品を選択してください --</option>
      <c:forEach var="p" items="${productList}">
        <option value="${p.product_id}"
          <c:if test="${edit && p.product_id == ri.product_id}">selected</c:if>>
          ${p.product_id} - ${p.product_name}
        </option>
      </c:forEach>
    </select>
  </div>

  <!-- 数量 (入力必須) / 수량 (필수 입력) -->
  <div class="row">
    <label>数量</label>
    <input type="number" name="quantity" min="1" value="${edit ? ri.quantity : 1}" required>
  </div>

  <!-- 入庫日 (カレンダー入力) / 입고일 (캘린더 입력) -->
  <div class="row">
    <label>入庫日</label>
    <c:if test="${edit && ri.receive_date ne null}">
      <fmt:formatDate value="${ri.receive_date}" pattern="yyyy-MM-dd" var="recvDate"/> <%-- 日付フォーマット / 날짜 변환 --%>
    </c:if>
    <input type="text" id="receive_date" name="receive_date" value="${recvDate}">
  </div>

  <!-- 業務状態 (検収中/正常/取消/返品) / 업무상태 (검수중/정상/취소/반품) -->
  <div class="row">
    <label>業務状態</label>
    <select name="receive_status">
      <c:choose>
        <c:when test="${not empty receiveStatusList}">
          <c:forEach var="st" items="${receiveStatusList}">
            <option value="${st}" <c:if test="${(edit && st == ri.receive_status) || (!edit && st == '正常')}">selected</c:if>>${st}</option>
          </c:forEach>
        </c:when>
        <c:otherwise>
          <c:set var="statusVal" value="${edit && not empty ri.receive_status ? ri.receive_status : '正常'}"/>
          <option value="検収中"   ${statusVal == '検収中' ? 'selected' : ''}>検収中</option>
          <option value="正常"     ${statusVal == '正常' ? 'selected' : ''}>正常</option>
          <option value="入庫取消" ${statusVal == '入庫取消' ? 'selected' : ''}>入庫取消</option>
          <option value="返品処理" ${statusVal == '返品処理' ? 'selected' : ''}>返品処理</option>
        </c:otherwise>
      </c:choose>
    </select>
  </div>

  <!-- アクションボタン / 액션 버튼 -->
  <div class="actions">
    <button type="submit" class="btn">${edit ? '修正保存' : '登録'}</button> <%-- 수정 시: 修正保存 / 신규: 登録 --%>
    <a href="<c:url value='/listReceiveInfos.do'/>" class="btn">リスト</a> <%-- リストに戻る / 리스트로 돌아가기 --%>
  </div>
</form>

</body>
</html>

