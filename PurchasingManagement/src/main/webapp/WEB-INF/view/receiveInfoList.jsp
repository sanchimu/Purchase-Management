<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>입고 목록</title>
<style>
  body{font-family:Arial,sans-serif;margin:20px;}
  h2{margin-bottom:10px;}
  .toolbar{display:flex;gap:8px;align-items:center;margin:10px 0 16px;flex-wrap:wrap;}
  .toolbar form{display:flex;gap:8px;align-items:center;}
  input[type="text"]{padding:6px 8px;}
  input[type="submit"],a.btn,button.btn{padding:6px 10px;border:1px solid #ccc;background:#f7f7f7;text-decoration:none;color:#333;cursor:pointer;}
  table{border-collapse:collapse;width:100%;}
  th,td{border:1px solid #ddd;padding:8px;text-align:center;}
  th{background:#f2f2f2;}
</style>
<script>
  function toggleAll(source){
    const boxes=document.querySelectorAll('input[name="receiveIds"]');
    boxes.forEach(cb=>cb.checked=source.checked);
  }
  function ensureChecked(){
    const checked=document.querySelectorAll('input[name="receiveIds"]:checked');
    if(checked.length===0){ alert('항목을 하나 이상 선택하세요.'); return false; }
    return true;
  }
</script>
</head>
<body>

<h2>입고 목록</h2>

<div class="toolbar">
  <a class="btn" href="<c:url value='/addReceiveInfo.do'/>">입고 등록</a>
  <a class="btn" href="<c:url value='/listReceiveInfos.do'/>">새로고침</a>
</div>

<form action="<c:url value='/updateReceiveInfos.do'/>" method="post" onsubmit="return ensureChecked();">
  <table>
    <tr>
      <th><input type="checkbox" onclick="toggleAll(this)"></th>
      <th>입고 ID</th>
      <th>주문 ID</th>
      <th>상품 ID</th>
      <th>수량</th>
      <th>입고일자</th>
      <th>업무상태</th>
    </tr>

    <c:choose>
      <c:when test="${empty receiveList}">
        <tr><td colspan="7">조회된 입고 정보가 없습니다.</td></tr>
      </c:when>
      <c:otherwise>
        <c:forEach var="r" items="${receiveList}">
          <tr>
            <td><input type="checkbox" name="receiveIds" value="${r.receive_id}"></td>
            <td>${r.receive_id}</td>
            <td>${r.order_id}</td>
            <td>${r.product_id}</td>
            <td>${r.quantity}</td>
            <td><fmt:formatDate value="${r.receive_date}" pattern="yyyy-MM-dd"/></td>
            <td>
              <select name="status_${r.receive_id}">
                <c:forEach var="st" items="${receiveStatusList}">
                  <option value="${st}" <c:if test="${st == r.receive_status}">selected</c:if>>${st}</option>
                </c:forEach>
              </select>
            </td>
          </tr>
        </c:forEach>
      </c:otherwise>
    </c:choose>
  </table>

  <div style="margin-top:10px;">
    <button type="submit" class="btn">선택 항목 업무상태 저장</button>
  </div>
</form>

</body>
</html>
