<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>발주서 목록</title>
<style>
  body{font-family:Arial,Helvetica,sans-serif;margin:20px;}
  h2{margin:0 0 12px;}
  .toolbar{display:flex;gap:8px;align-items:center;margin:8px 0 16px;flex-wrap:wrap}
  .btn{padding:6px 10px;border:1px solid #ccc;background:#f7f7f7;cursor:pointer;text-decoration:none;color:#333;display:inline-block}
  table{border-collapse:collapse;width:100%}
  th,td{border:1px solid #ddd;padding:8px;text-align:center}
  th{background:#f2f2f2}
  .pill{display:inline-block;min-width:40px;padding:2px 8px;border-radius:999px;font-size:12px}
  .pill-a{background:#e7f6e9;color:#1c7c2c} /* 진행(A) */
  .pill-x{background:#fdeaea;color:#c62828} /* 중단(X) */
</style>
<script>
  function toggleAll(src){
    document.querySelectorAll('input[name="orderIds"]').forEach(cb=>cb.checked=src.checked);
  }
</script>
</head>
<body>

<h2>발주서 목록</h2>

<div class="toolbar">
  <form method="get" action="<c:url value='/orderSheetList.do'/>" style="display:flex;align-items:center;gap:6px">
    <label>
      <input type="checkbox" name="includeHidden" value="1" <c:if test="${includeHidden}">checked</c:if> />
      중단 포함
    </label>
    <button type="submit" class="btn">적용</button>
  </form>

  <a class="btn" href="<c:url value='/orderSheetList.do'/>?includeHidden=${includeHidden ? 1 : 0}">새로고침</a>
  <a class="btn" href="<c:url value='/addOrderSheet.do'/>">발주서 등록</a>
</div>

<form method="post">
  <table>
    <tr>
      <th style="width:40px"><input type="checkbox" onclick="toggleAll(this)"></th>
      <th>발주 ID</th>
      <th>발주일</th>
      <th>상품 ID</th>
      <th>상품명</th>
      <th>요청수량</th>
      <th>요청자</th>
      <th>공급업체 ID</th>
      <th>공급업체명</th>
      <th>업무상태</th>
      <th>표시상태</th>
    </tr>

    <c:choose>
      <c:when test="${empty orderList}">
        <tr><td colspan="11">조회된 발주서가 없습니다.</td></tr>
      </c:when>
      <c:otherwise>
        <c:forEach var="o" items="${orderList}">
          <tr>
            <td><input type="checkbox" name="orderIds" value="${o.order_id}"></td>
            <td>${o.order_id}</td>
            <td><fmt:formatDate value="${o.order_date}" pattern="yyyy-MM-dd"/></td>
            <td>${o.product_id}</td>
            <td>${o.product_name}</td>
            <td>${o.request_quantity}</td>
            <td>${o.requester_name}</td>
            <td>${o.supplier_id}</td>
            <td>${o.supplier_name}</td>
            <td>
              <select name="status_${o.order_id}">
                <c:forEach var="st" items="${orderStatusList}">
                  <option value="${st}" <c:if test="${st == o.order_status}">selected</c:if>>${st}</option>
                </c:forEach>
              </select>
            </td>
            <td>
              <c:choose>
                <c:when test="${empty o.row_status || o.row_status == 'A'}">
                  <span class="pill pill-a">진행</span>
                </c:when>
                <c:otherwise>
                  <span class="pill pill-x">중단</span>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
        </c:forEach>
      </c:otherwise>
    </c:choose>
  </table>

  <div style="margin-top:10px;display:flex;gap:8px;flex-wrap:wrap;">
    <button type="submit"
            class="btn"
            formaction="<c:url value='/updateOrderSheet.do'/>"
            formmethod="post">
      선택 항목 업무상태 저장
    </button>

    <button type="submit"
            class="btn"
            formaction="<c:url value='/orderSheetStopAX.do'/>"
            formmethod="post">
      선택 항목 중단(A/X)
    </button>

    <button type="submit"
            class="btn"
            formaction="<c:url value='/orderSheetResumeAX.do'/>"
            formmethod="post">
      선택 항목 재개(A/X)
    </button>
  </div>
</form>

</body>
</html>
