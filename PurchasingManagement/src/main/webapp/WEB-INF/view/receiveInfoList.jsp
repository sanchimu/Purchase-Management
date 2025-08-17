<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

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
  input[type="text"], input[type="date"]{padding:6px 8px;}
  input[type="submit"],a.btn,button.btn{padding:6px 10px;border:1px solid #ccc;background:#f7f7f7;text-decoration:none;color:#333;cursor:pointer;}
  table{border-collapse:collapse;width:100%;}
  th,td{border:1px solid #ddd;padding:8px;text-align:center;}
  th{background:#f2f2f2;}
  .right{text-align:right;}
  .muted{color:#666;}
</style>
<script>
  function toggleAll(source){
    const boxes=document.querySelectorAll('input[name="receiveIds"]');
    boxes.forEach(cb=>cb.checked=source.checked);
  }
  function ensureChecked(){
    const checked=document.querySelectorAll('input[name="receiveIds"]:checked');
    if(checked.length===0){ alert('항목을 하나 이상 선택하세요.'); return false; }
    return confirm('선택한 항목의 업무상태를 저장하시겠습니까?');
  }
</script>
</head>
<body>

<h2>입고 목록</h2>

<div class="toolbar">
  <a class="btn" href="<c:url value='/addReceiveInfo.do'/>">입고 등록</a>
  <a class="btn" href="<c:url value='/listReceiveInfos.do'/>">새로고침</a>

  <!-- 간단 검색 -->
  <form action="<c:url value='/listReceiveInfos.do'/>" method="get">
    <input type="text" name="productName" value="${fn:escapeXml(param.productName)}" placeholder="상품명"/>
    <input type="text" name="supplierName" value="${fn:escapeXml(param.supplierName)}" placeholder="공급업체"/>
    <input type="date" name="fromDate" value="${param.fromDate}"/>
    ~
    <input type="date" name="toDate" value="${param.toDate}"/>
    <input type="submit" class="btn" value="검색"/>
  </form>
</div>

<form action="<c:url value='/updateReceiveInfos.do'/>" method="post" onsubmit="return ensureChecked();">
  <table>
    <tr>
      <th><input type="checkbox" onclick="toggleAll(this)"></th>
      <th>입고 ID</th>
      <th>주문 ID</th>
      <th>상품 ID</th>
      <th>상품명</th>
      <th>공급업체</th>
      <th class="right">수량</th>
      <th>입고일자</th>
      <th class="right">반품가능</th>
      <th>업무상태</th>
    </tr>

    <c:choose>
      <c:when test="${empty receiveList}">
        <tr><td colspan="10">조회된 입고 정보가 없습니다.</td></tr>
      </c:when>
      <c:otherwise>
        <c:forEach var="r" items="${receiveList}">
          <tr>
            <td><input type="checkbox" name="receiveIds" value="${r.receive_id}"></td>
            <td>${r.receive_id}</td>
            <td>${r.order_id}</td>
            <td>${r.product_id}</td>
            <td>${empty r.product_name ? '-' : r.product_name}</td>
            <td>${empty r.supplier_name ? '-' : r.supplier_name}</td>
            <td class="right"><fmt:formatNumber value="${r.quantity}"/></td>
            <td><fmt:formatDate value="${r.receive_date}" pattern="yyyy-MM-dd"/></td>
            <td class="right"><fmt:formatNumber value="${r.available_to_return}"/></td>
            <td>
              <select name="status_${r.receive_id}">
                <c:choose>
                  <c:when test="${not empty receiveStatusList}">
                    <c:forEach var="st" items="${receiveStatusList}">
                      <option value="${st}" <c:if test="${st == r.receive_status}">selected</c:if>>${st}</option>
                    </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <option value="검수중"  <c:if test="${r.receive_status == '검수중'}">selected</c:if>>검수중</option>
                    <option value="정상"    <c:if test="${r.receive_status == '정상'}">selected</c:if>>정상</option>
                    <option value="입고 취소" <c:if test="${r.receive_status == '입고 취소'}">selected</c:if>>입고 취소</option>
                    <option value="반품 처리" <c:if test="${r.receive_status == '반품 처리'}">selected</c:if>>반품 처리</option>
                  </c:otherwise>
                </c:choose>
              </select>
            </td>
          </tr>
        </c:forEach>
      </c:otherwise>
    </c:choose>
  </table>

  <div style="margin-top:10px; display:flex; gap:8px; align-items:center;">
    <button type="submit" class="btn">선택 항목 업무상태 저장</button>
    <span class="muted">총 <strong>${fn:length(receiveList)}</strong> 건</span>
  </div>
</form>

</body>
</html>
