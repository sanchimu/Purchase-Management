<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>반품 목록</title>
<style>
  body{font-family:Arial,sans-serif;margin:20px;}
  h2{margin:0 0 12px;}
  .toolbar{display:flex;gap:10px;align-items:center;flex-wrap:wrap;margin:10px 0 16px;}
  .toolbar a.btn, .toolbar input[type="submit"], .toolbar button.btn{padding:6px 10px;border:1px solid #ccc;background:#f7f7f7;text-decoration:none;color:#333;cursor:pointer;}
  .toolbar form.search{display:flex;gap:8px;align-items:center;}
  .toolbar input[type="text"]{padding:6px 8px;}
  table{border-collapse:collapse;width:100%;}
  th,td{border:1px solid #ddd;padding:8px;text-align:center;}
  th{background:#f2f2f2;}
  .msg{margin:8px 0;padding:8px 10px;border-radius:4px;}
  .msg-ok{background:#e9f7ef;border:1px solid #c7ebd3;color:#1e7e34;}
  .msg-err{background:#fdecea;border:1px solid #f5c6cb;color:#a71d2a;white-space:pre-line;}
  .danger{border-color:#e0b4b4;background:#ffe8e6;}
  .inline-input{width:80px;}
  .inline-reason{width:240px;}
</style>
<script>
  function toggleAll(src){
    const boxes=document.querySelectorAll('input[name="returnInfoIds"]');
    boxes.forEach(cb=>cb.checked=src.checked);
  }
  function ensureChecked(){
    const checked=document.querySelectorAll('input[name="returnInfoIds"]:checked');
    if(checked.length===0){ alert('항목을 하나 이상 선택하세요.'); return false; }
    return true;
  }
</script>
</head>
<body>

<h2>반품 목록</h2>

<!-- 세션 메시지 -->
<c:if test="${not empty sessionScope.successMsg}">
  <div class="msg msg-ok">${sessionScope.successMsg}</div>
  <c:remove var="successMsg" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.errorMsg}">
  <div class="msg msg-err">${sessionScope.errorMsg}</div>
  <c:remove var="errorMsg" scope="session"/>
</c:if>

<div class="toolbar">
  <a class="btn" href="<c:url value='/addReturnInfos.do'/>">반품 등록</a>
  <a class="btn" href="<c:url value='/returnInfoList.do'/>">새로고침</a>

  <!-- 빠른 검색 -->
  <form class="search" action="<c:url value='/searchReturnInfo.do'/>" method="get">
    반품ID <input type="text" name="return_id"  value="${param.return_id}">
    입고ID <input type="text" name="receive_id" value="${param.receive_id}">
    상품ID <input type="text" name="product_id" value="${param.product_id}">
    <input type="submit" value="검색">
    <a class="btn" href="<c:url value='/returnInfoList.do'/>">전체보기</a>
  </form>
</div>

<form id="retForm" action="<c:url value='/updateReturnInfos.do'/>" method="post" onsubmit="return ensureChecked();">
  <table>
    <tr>
      <th><input type="checkbox" onclick="toggleAll(this)"></th>
      <th>반품ID</th>
      <th>입고ID</th>
      <th>상품ID</th>
      <th>반품수량</th>
      <th>사유</th>
      <th>반품일</th>
    </tr>

    <c:choose>
      <c:when test="${empty returnInfoList}">
        <tr><td colspan="7">조회된 반품 정보가 없습니다.</td></tr>
      </c:when>
      <c:otherwise>
        <c:forEach var="r" items="${returnInfoList}">
          <tr>
            <td><input type="checkbox" name="returnInfoIds" value="${r.return_id}"></td>
            <td>${r.return_id}</td>
            <td>${r.receive_id}</td>
            <td>${r.product_id}</td>
            <td>
              <input type="text" class="inline-input" name="qty_${r.return_id}" value="${r.quantity}">
            </td>
            <td>
              <input type="text" class="inline-reason" name="reason_${r.return_id}" value="${r.reason}">
            </td>
            <td><fmt:formatDate value="${r.return_date}" pattern="yyyy-MM-dd"/></td>
          </tr>
        </c:forEach>
      </c:otherwise>
    </c:choose>
  </table>

  <div style="margin-top:10px; display:flex; gap:8px;">
    <button type="submit" class="btn">선택 항목 저장</button>
    <button type="submit"
            class="btn danger"
            formaction="<c:url value='/deleteReturnInfos.do'/>"
            formmethod="post"
            onclick="return ensureChecked() && confirm('선택한 항목을 삭제하시겠습니까?');">
      선택 항목 삭제
    </button>
  </div>
</form>

</body>
</html>
