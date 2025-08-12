<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>구매요청 목록</title>
<style>
  body { font-family: Arial, sans-serif; margin: 20px; }
  h2 { color: #333; }
  .toolbar { display:flex; gap:12px; align-items:center; margin:10px 0 16px; }
  .toolbar form { display:flex; gap:8px; align-items:center; }
  input[type="text"] { padding:6px 8px; }
  input[type="submit"], a.btn { padding:6px 10px; border:1px solid #ccc; background:#f7f7f7; text-decoration:none; color:#333; cursor:pointer; }
  .msg { margin:8px 0; padding:8px 10px; background:#fff8d6; border:1px solid #eedc82; }
  table { border-collapse: collapse; width: 100%; max-width: 900px; }
  th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }
  th { background:#f2f2f2; }
</style>
</head>
<body>

<h2>구매요청 목록</h2>

<div class="toolbar">
  <a class="btn" href="<c:url value='/insertpurchaserequest.do'/>">구매요청 등록</a>
  <a class="btn" href="<c:url value='/searchpurchaserequest.do'/>">상품ID로 구매요청 조회</a>
  <a class="btn" href="<c:url value='/requestList.do'/>">새로고침</a>
</div>

<c:if test="${not empty message}">
  <div class="msg">${message}</div>
</c:if>

<table>
  <tr>
    <th>요청ID</th>
    <th>상품ID</th>
    <th>수량</th>
    <th>요청일</th>
    <th>요청자</th>
    <th>삭제</th>
  </tr>

  <c:if test="${empty requestList}">
    <tr>
      <td colspan="6">데이터가 없습니다.</td>
    </tr>
  </c:if>

  <c:forEach var="req" items="${requestList}">
    <tr>
      <td>${req.request_id}</td>
      <td>${req.product_id}</td>
      <td>${req.quantity}</td>
      <td>${req.request_date}</td>
      <td>${req.requester_name}</td>
      <td>
        <form action="<c:url value='/deletepurchaserequest.do'/>" method="post" style="margin:0">
          <input type="hidden" name="request_id" value="${req.request_id}">
          <input type="submit" value="삭제" onclick="return confirm('삭제하시겠습니까?');">
        </form>
      </td>
    </tr>
  </c:forEach>
</table>

</body>
</html>
