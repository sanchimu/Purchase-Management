<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="edit" value="${not empty ri}"/>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${edit ? '입고 수정' : '입고 등록'}</title>
<style>
  body { font-family: Arial, sans-serif; margin: 20px; }
  .form { max-width: 640px; padding: 16px; border: 1px solid #ddd; border-radius: 6px; }
  .row { display:flex; align-items:center; margin:10px 0; gap:10px; }
  .row label { width: 140px; text-align: right; font-weight:600; }
  input[type="text"], input[type="number"], input[type="date"], select { flex:1; padding:8px 10px; }
  .actions { margin-top: 16px; display:flex; gap:10px; }
  .btn { padding:8px 12px; border:1px solid #ccc; background:#f7f7f7; text-decoration:none; color:#333; cursor:pointer; }
  .hint { color:#666; font-size:12px; margin-left:140px; }
</style>
</head>
<body>

<h2>${edit ? '입고 수정' : '입고 등록'}</h2>

<form class="form" action="<c:url value='/addReceiveInfo.do'/>" method="post">
  <c:if test="${edit}">
    <input type="hidden" name="receive_id" value="${ri.receive_id}"/>
  </c:if>
  
<c:if test="${not empty errorMsg}">
  <div style="color:#b00020; font-weight:bold; margin-bottom:10px;">
    ${errorMsg}
  </div>
</c:if>

<div class="hint">※ 주문 ID와 상품 ID는 반드시 사전에 등록된 값만 입력해야 합니다.</div>

  <div class="row">
    <label>주문 ID</label>
    <input type="text" name="order_id" value="${edit ? ri.order_id : ''}" ${edit ? 'readonly' : 'required'} placeholder="OR001">
  </div>

  <div class="row">
    <label>상품 ID</label>
    <input type="text" name="product_id" value="${edit ? ri.product_id : ''}" ${edit ? 'readonly' : 'required'} placeholder="P001">
  </div>

  <div class="row">
    <label>수량</label>
    <input type="number" name="quantity" min="1" value="${edit ? ri.quantity : 1}" required>
  </div>

  <div class="row">
    <label>입고일</label>
    <c:if test="${edit && ri.receive_date ne null}">
      <fmt:formatDate value="${ri.receive_date}" pattern="yyyy-MM-dd" var="recvDate"/>
    </c:if>
    <input type="date" name="receive_date" value="${recvDate}">
  </div>

  <div class="row">
    <label>업무상태</label>
    <select name="receive_status">
      <c:choose>
        <c:when test="${not empty receiveStatusList}">
          <c:forEach var="st" items="${receiveStatusList}">
            <option value="${st}" <c:if test="${(edit && st == ri.receive_status) || (!edit && st == '정상')}">selected</c:if>>${st}</option>
          </c:forEach>
        </c:when>
        <c:otherwise>
          <c:set var="statusVal" value="${edit && not empty ri.receive_status ? ri.receive_status : '정상'}"/>
          <option value="검수중"   ${statusVal == '검수중' ? 'selected' : ''}>검수중</option>
          <option value="정상"     ${statusVal == '정상' ? 'selected' : ''}>정상</option>
          <option value="입고 취소" ${statusVal == '입고 취소' ? 'selected' : ''}>입고 취소</option>
          <option value="반품 처리" ${statusVal == '반품 처리' ? 'selected' : ''}>반품 처리</option>
        </c:otherwise>
      </c:choose>
    </select>
  </div>



  <div class="actions">
    <button type="submit" class="btn">${edit ? '수정 저장' : '등록'}</button>
    <a href="<c:url value='/listReceiveInfos.do'/>" class="btn">목록</a>
  </div>
</form>

</body>
</html>