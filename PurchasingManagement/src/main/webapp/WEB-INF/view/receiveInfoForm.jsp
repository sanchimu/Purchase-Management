<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="edit" value="${not empty ri}"/>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>${edit ? '入庫修正' : '入庫登録'}</title>
<style>
  body { font-family: Arial, sans-serif; margin: 20px; }
  .form { max-width: 640px; padding: 16px; border: 1px solid #ddd; border-radius: 6px; }
  .row { display:flex; align-items:center; margin:10px 0; gap:10px; }
  .row label { width: 140px; text-align: right; font-weight:600; }
  input[type="text"], input[type="number"], input[type="date"], input[type="text"].datepicker, select { flex:1; padding:8px 10px; }
  .actions { margin-top: 16px; display:flex; gap:10px; }
  .btn { padding:8px 12px; border:1px solid #ccc; background:#f7f7f7; text-decoration:none; color:#333; cursor:pointer; }
  .hint { color:#666; font-size:12px; margin-left:140px; }
</style>

<!-- flatpickr (カレンダーライブラリ) -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
<script src="https://cdn.jsdelivr.net/npm/flatpickr/dist/l10n/ja.js"></script>

<script>
  document.addEventListener("DOMContentLoaded", function() {
    flatpickr("#receive_date", {
      locale: "ja",
      dateFormat: "Y年m月d日"
    });
  });
</script>
</head>
<body>

<h2>${edit ? '入庫修正' : '入庫登録'}</h2>

<form class="form" action="<c:url value='/addReceiveInfo.do'/>" method="post">
  <c:if test="${edit}">
    <input type="hidden" name="receive_id" value="${ri.receive_id}"/>
  </c:if>
  
<c:if test="${not empty errorMsg}">
  <div style="color:#b00020; font-weight:bold; margin-bottom:10px;">
    ${errorMsg}
  </div>
</c:if>

<div class="hint">※ 注文IDと商品IDは必ず事前に登録された値のみ入力してください。</div>

  <div class="row">
    <label>注文ID</label>
    <input type="text" name="order_id" value="${edit ? ri.order_id : ''}" ${edit ? 'readonly' : 'required'} placeholder="OR001">
  </div>

  <div class="row">
    <label>商品ID</label>
    <input type="text" name="product_id" value="${edit ? ri.product_id : ''}" ${edit ? 'readonly' : 'required'} placeholder="P001">
  </div>

  <div class="row">
    <label>数量</label>
    <input type="number" name="quantity" min="1" value="${edit ? ri.quantity : 1}" required>
  </div>

  <div class="row">
    <label>入庫日</label>
    <c:if test="${edit && ri.receive_date ne null}">
      <fmt:formatDate value="${ri.receive_date}" pattern="yyyy-MM-dd" var="recvDate"/>
    </c:if>
    <!-- flatpickr 적용 input -->
    <input type="text" id="receive_date" name="receive_date" value="${recvDate}">
  </div>

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

  <div class="actions">
    <button type="submit" class="btn">${edit ? '修正保存' : '登録'}</button>
    <a href="<c:url value='/listReceiveInfos.do'/>" class="btn">リスト</a>
  </div>
</form>

</body>
</html>
