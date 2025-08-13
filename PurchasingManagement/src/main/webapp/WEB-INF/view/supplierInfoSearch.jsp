<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>공급업체 검색</title>
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

<h2>공급업체 검색 (이름)</h2>

<div class="toolbar">
  <!-- POST로 핸들러에 전송 -->
  <form action="<c:url value='/searchsupplier.do'/>" method="post">
    <label for="supplier_name">공급업체 이름</label>
    <input type="text" id="supplier_name" name="supplier_name"
           placeholder="공급업체명 입력"
           value="${supplier_name}" />
    <input type="submit" value="검색" />
    <a class="btn" href="<c:url value='/searchsupplier.do'/>">초기화</a>
  </form>

  <a class="btn" href="<c:url value='/insertsupplier.do'/>">공급업체 등록</a>
  <a class="btn" href="<c:url value='/listsupplier.do'/>">목록으로</a>
</div>

<c:if test="${not empty message}">
  <div class="msg">${message}</div>
</c:if>

<c:if test="${not empty supplierList}">
  <table>
    <tr>
      <th>공급업체 ID</th>
      <th>공급업체 이름</th>
      <th>연락처</th>
      <th>주소</th>
      <th>삭제</th>
    </tr>
    <c:forEach var="supplier" items="${supplierList}">
      <tr>
        <td>${supplier.supplier_id}</td>
        <td>${supplier.supplier_name}</td>
        <td>${supplier.contact_number}</td>
        <td>${supplier.address}</td>
        <td>
          <form action="<c:url value='/deletesupplier.do'/>" method="post" style="margin:0">
            <input type="hidden" name="supplier_id" value="${supplier.supplier_id}"/>
            <input type="submit" value="삭제" onclick="return confirm('삭제하시겠습니까?');"/>
          </form>
        </td>
      </tr>
    </c:forEach>
  </table>
</c:if>

</body>
</html>
