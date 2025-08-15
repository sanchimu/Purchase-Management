<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>입고 등록</title>
<style>
  body { font-family: Arial, sans-serif; margin: 20px; }
  .form { max-width: 520px; padding: 16px; border: 1px solid #ddd; }
  .row { display:flex; align-items:center; margin:8px 0; gap:10px; }
  .row label { width: 120px; text-align: right; }
  input[type="text"], input[type="number"], input[type="date"], select { flex:1; padding:6px 8px; }
  .actions { margin-top: 12px; display:flex; gap:8px; }
  .btn { padding:6px 12px; border:1px solid #ccc; background:#f7f7f7; text-decoration:none; color:#333; cursor:pointer; }
</style>
</head>
<body>

<h2>입고 등록</h2>

<form class="form" action="<c:url value='/addReceiveInfo.do'/>" method="post">
  <div class="row">
    <label>주문 ID</label>
    <input type="text" name="order_id" required>
  </div>

  <div class="row">
    <label>상품 ID</label>
    <input type="text" name="product_id" required>
  </div>

  <div class="row">
    <label>수량</label>
    <input type="number" name="quantity" min="1" value="1" required>
  </div>

  <div class="row">
    <label>입고일</label>
    <input type="date" name="receive_date">
  </div>

  <!-- 선택: 등록 시 상태를 기본 '정상' 외로 설정하고 싶을 때 사용 -->
  <div class="row">
    <label>업무상태</label>
    <select name="receive_status">
      <c:forEach var="st" items="${receiveStatusList}">
        <option value="${st}" <c:if test="${st == '정상'}">selected</c:if>>${st}</option>
      </c:forEach>
    </select>
  </div>

  <div class="actions">
    <button type="submit" class="btn">등록</button>
    <a href="<c:url value='/listReceiveInfos.do'/>" class="btn">목록</a>
  </div>
</form>

</body>
</html>
