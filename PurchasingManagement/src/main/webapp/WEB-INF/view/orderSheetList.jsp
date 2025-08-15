<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title>발주서 목록</title>
<style>
  body { font-family: Arial, sans-serif; margin: 20px; }
  h2 { margin: 0 0 12px; }
  .toolbar { display:flex; gap:10px; align-items:center; margin:10px 0 16px; flex-wrap: wrap; }
  .toolbar .spacer { flex:1; }
  .btn { padding:6px 10px; border:1px solid #ccc; background:#f7f7f7; cursor:pointer; }
  .btn-primary { background:#2d7ef7; color:#fff; border-color:#2d7ef7; }
  .btn-danger { background:#e74c3c; color:#fff; border-color:#e74c3c; }
  .btn-success { background:#27ae60; color:#fff; border-color:#27ae60; }
  table { border-collapse: collapse; width: 100%; }
  th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }
  th { background:#f2f2f2; }
  .badge-a { background:#e6f6e6; padding:2px 8px; border-radius:10px; }
  .badge-x { background:#fdeaea; padding:2px 8px; border-radius:10px; }
</style>
<script>
  function toggleAll(source) {
    const checks = document.querySelectorAll("input[name='orderIds']");
    checks.forEach(cb => cb.checked = source.checked);
  }

  function submitUpdate() {
    const picked = document.querySelectorAll("input[name='orderIds']:checked");
    if (picked.length === 0) { alert("업무상태를 저장할 발주를 선택하세요."); return; }
    document.getElementById("formUpdate").submit();
  }

  function submitBulk(toVal) {
    const picked = document.querySelectorAll("input[name='orderIds']:checked");
    if (picked.length === 0) { alert("표시상태를 변경할 발주를 선택하세요."); return; }

    const bulkForm = document.getElementById("formBulk");
    const holder   = document.getElementById("bulkIds");
    holder.innerHTML = "";
    picked.forEach(cb => {
      const hid = document.createElement("input");
      hid.type = "hidden";
      hid.name = "ids";              // 공용 핸들러 규약
      hid.value = cb.value;          // order_id
      holder.appendChild(hid);
    });

    document.getElementById("bulkTo").value = toVal; // 'A' or 'X'
    bulkForm.submit();
  }
</script>
</head>
<body>

<h2>발주서 목록</h2>

<!-- 상단 툴바 -->
<div class="toolbar">
  <!-- 표시상태 토글 -->
  <label>
    <input type="checkbox"
           onchange="location.href='<c:url value="/orderSheetList.do"/>' + (this.checked ? '?includeHidden=1' : '')"
           <c:if test="${includeHidden}">checked</c:if> />
    중단 포함
  </label>

  <div class="spacer"></div>

  <!-- 표시상태(A/X) 일괄 변경: 공용 핸들러로 전송 -->
  <button type="button" class="btn btn-danger" onclick="submitBulk('X')">선택 항목 중단(A/X)</button>
  <button type="button" class="btn btn-success" onclick="submitBulk('A')">선택 항목 재개(A/X)</button>

  <!-- 업무상태 저장 -->
  <button type="button" class="btn btn-primary" onclick="submitUpdate()">선택 항목 업무상태 저장</button>

  <!-- 등록 페이지 이동 -->
  <a class="btn" href="<c:url value='/addOrderSheet.do'/>">발주서 등록</a>
</div>

<!-- 업무상태 저장용 메인 폼 -->
<form id="formUpdate" action="<c:url value='/updateOrderSheet.do'/>" method="post">
  <table>
    <tr>
      <th><input type="checkbox" onclick="toggleAll(this)" /></th>
      <th>발주 ID</th>
      <th>구매요청 ID</th>
      <th>공급업체 ID</th>
      <th>발주일</th>
      <th>업무상태</th>
      <th>표시상태</th>
    </tr>

    <c:if test="${empty orderList}">
      <tr>
        <td colspan="7">데이터가 없습니다.</td>
      </tr>
    </c:if>

    <c:forEach var="order" items="${orderList}">
      <tr>
        <td>
          <input type="checkbox" name="orderIds" value="${order.order_id}" />
        </td>
        <td>${order.order_id}</td>
        <td>${order.request_id}</td>
        <td>${order.supplier_id}</td>
        <td><fmt:formatDate value="${order.order_date}" pattern="yyyy-MM-dd" /></td>
        <td>
          <select name="status_${order.order_id}">
            <c:forEach var="st" items="${orderStatusList}">
              <option value="${st}" <c:if test="${st == order.order_status}">selected</c:if>>${st}</option>
            </c:forEach>
          </select>
        </td>
        <td>
          <c:choose>
            <c:when test="${order.row_status == 'X'}"><span class="badge-x">중단</span></c:when>
            <c:otherwise><span class="badge-a">진행</span></c:otherwise>
          </c:choose>
        </td>
      </tr>
    </c:forEach>
  </table>
</form>

<!-- 표시상태(A/X) 일괄 변경용 보조 폼 (공용 핸들러 규약) -->
<form id="formBulk" action="<c:url value='/status/bulk.do'/>" method="post" style="display:none;">
  <input type="hidden" name="table" value="order_sheet" />
  <input type="hidden" name="idColumn" value="order_id" />
  <input type="hidden" id="bulkTo" name="to" value="" />
  <div id="bulkIds"></div> <!-- 여기 안에 ids hidden이 채워짐 -->
</form>

</body>
</html>
