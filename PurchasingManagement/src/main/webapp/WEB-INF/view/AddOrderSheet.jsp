<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>발주서 등록</title>
<style>
  body{font-family:Arial,Helvetica,sans-serif;margin:20px;}
  .row{margin:10px 0;}
  label{display:inline-block;width:130px}
  select,input{padding:6px 8px}
  .btn{padding:6px 10px;border:1px solid #ccc;background:#f7f7f7;cursor:pointer;text-decoration:none;color:#333}
  .msg{margin:8px 0;padding:8px 10px;border:1px solid #eedc82;background:#fff8d6}
</style>
<script>
  function onReqChange(sel){
    const opt = sel.options[sel.selectedIndex];
    document.getElementById('supplier_id').value = opt.getAttribute('data-supplier') || '';
  }
</script>
</head>
<body>

<h2>발주서 등록</h2>

<c:if test="${not empty error}">
  <div class="msg">${error}</div>
</c:if>

<form method="post" action="<c:url value='/addOrderSheet.do'/>">
  <div class="row">
    <label>요청 ID</label>
    <select name="request_id" id="request_id" onchange="onReqChange(this)" required>
      <option value="">-- 선택 --</option>
      <c:forEach var="o" items="${requestOptions}">
        <option value="${o.request_id}" data-supplier="${o.supplier_id}">
          ${o.request_id} / ${o.product_id} - ${o.product_name} (요청자:${o.requester_name}, 수량:${o.request_quantity})
        </option>
      </c:forEach>
    </select>
  </div>

  <div class="row">
    <label>공급업체 ID</label>
    <input type="text" id="supplier_id" value="" readonly />
    <span style="color:#999;font-size:12px">* 자동 채움(서버에서도 검증)</span>
  </div>

  <div class="row">
    <label>업무 상태</label>
    <select name="order_status">
      <c:forEach var="st" items="${orderStatusList}">
        <option value="${st}">${st}</option>
      </c:forEach>
    </select>
  </div>

  <div class="row">
    <button type="submit" class="btn">등록</button>
    <a class="btn" href="<c:url value='/orderSheetList.do'/>">목록</a>
  </div>
</form>

</body>
</html>
