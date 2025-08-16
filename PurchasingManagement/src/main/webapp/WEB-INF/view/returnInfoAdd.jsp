<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>반품 등록</title>
<style>
  body{font-family:Arial,sans-serif;margin:20px;}
  h2{margin-bottom:10px;}
  .toolbar{margin-bottom:12px;}
  a.btn,button.btn,input[type="submit"]{padding:6px 10px;border:1px solid #ccc;background:#f7f7f7;text-decoration:none;color:#333;cursor:pointer;}
  table{border-collapse:collapse;width:100%;}
  th,td{border:1px solid #ddd;padding:8px;text-align:center;}
  th{background:#f2f2f2;}
  .hint{color:#777;margin-left:8px;}
  .error{margin:10px 0;padding:8px 10px;color:#a00;background:#ffecec;border:1px solid #f5c2c2;}
</style>
<script>
  // 라디오 선택 시 "반품 가능 수량" 안내 및 max 제약 동기화
  function onPickChange(e){
    const avail = e.target.dataset.available ? parseInt(e.target.dataset.available,10) : 0;
    const qty   = document.getElementById('qty');
    const txt   = document.getElementById('availTxt');
    qty.max = Math.max(avail, 0);
    txt.textContent = '(반품 가능 수량: ' + avail + ')';
    if (qty.value === '' || parseInt(qty.value, 10) > avail) qty.value = Math.min(1, avail) || 0;
  }
  function ensurePicked(){
    const picked = document.querySelector('input[name="receiveId"]:checked');
    if(!picked){ alert('반품할 입고 항목을 선택하세요.'); return false; }
    return true;
  }
  window.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('input[name="receiveId"]').forEach(r => {
      r.addEventListener('change', onPickChange);
    });
  });
</script>
</head>
<body>

<h2>반품 등록</h2>

<div class="toolbar">
  <a class="btn" href="<c:url value='/returnInfoList.do'/>">← 반품 목록</a>
</div>

<c:if test="${not empty error}">
  <div class="error">${error}</div>
</c:if>

<form method="post" action="<c:url value='/returnProcess.do'/>" onsubmit="return ensurePicked();">

  <table>
    <tr>
      <th>선택</th>
      <th>입고ID</th>
      <th>주문ID</th>
      <th>상품ID</th>
      <th>총 입고수량</th>
      <th>반품 가능수량</th>
      <th>입고일자</th>
    </tr>

    <c:forEach var="ri" items="${receiveInfoList}">
      <tr>
        <td>
          <!-- name을 receiveId 로 맞춤 -->
          <input type="radio"
                 name="receiveId"
                 value="${ri.receive_id}"
                 data-available="${ri.available_to_return}">
        </td>
        <td>${ri.receive_id}</td>
        <td>${ri.order_id}</td>
        <td>${ri.product_id}</td>
        <td>${ri.quantity}</td>
        <td>${ri.available_to_return}</td>
        <td><fmt:formatDate value="${ri.receive_date}" pattern="yyyy-MM-dd"/></td>
      </tr>
    </c:forEach>

    <c:if test="${empty receiveInfoList}">
      <tr><td colspan="7">반품 가능한 입고 데이터가 없습니다.</td></tr>
    </c:if>
  </table>

  <div style="margin-top:12px; display:flex; gap:10px; align-items:center;">
    <label>반품 수량
      <input id="qty" type="number" name="quantity" min="1" value="1" required>
      <span id="availTxt" class="hint"></span>
    </label>

    <label>반품 사유
      <input type="text" name="reason" placeholder="반품 사유를 입력하세요." style="width:280px;" required>
    </label>

    <button type="submit" class="btn">반품 등록</button>
  </div>
</form>

</body>
</html>
