<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>상품 목록</title>
<style>
.table{width:100%;border-collapse:collapse}
.table th,.table td{border:1px solid #ddd;padding:8px;text-align:center}
.badge-ok{padding:2px 8px;border-radius:10px;background:#e6f6e6}
.badge-stop{padding:2px 8px;border-radius:10px;background:#fdeaea}
.muted{opacity:.55}
.toolbar button{margin-right:6px}
</style>
<script>
function validateSelection(){
  const checked = document.querySelectorAll('input[name="ids"]:checked');
  if(checked.length === 0){ alert('대상 상품을 선택하세요.'); return false; }
  return true;
}
function toggleAll(source){
  document.querySelectorAll('input[name="ids"]').forEach(cb => cb.checked = source.checked);
}
</script>
</head>
<body>

  <h2>상품 조회</h2>
  <form action="${pageContext.request.contextPath}/productList.do" method="get">
    상품 ID : <input type="text" name="product_id" value="${param.product_id}">
    상품명 : <input type="text" name="product_name" value="${param.product_name}">
    <br>
    카테고리 :
    <select name="category">
      <option value="">-- 선택 --</option>
      <c:forEach items="${categoryList}" var="category">
        <option value="${category}" <c:if test="${param.category == category}">selected</c:if>>${category}</option>
      </c:forEach>
    </select>
    공급업체 ID : <input type="text" name="supplier_id" value="${param.supplier_id}">
    <br>
    <input type="submit" value="상품 조회">
  </form>

  <h2>상품 목록</h2>

  <!-- 한 폼에서: 업무상태 저장 / 표시상태(A/X) 일괄변경 모두 처리 -->
  <form method="post">
    <input type="hidden" name="table" value="product"/>
    <input type="hidden" name="idColumn" value="product_id"/>

    <div class="toolbar" style="margin:10px 0;">
      <!-- 업무상태 저장 -->
      <button type="submit"
              formaction="${pageContext.request.contextPath}/updateProducts.do"
              onclick="return validateSelection();">선택항목 업무상태 저장</button>

      <!-- 표시상태(A/X) 일괄변경 -->
      <button type="submit"
              formaction="${pageContext.request.contextPath}/status/bulk.do"
              name="to" value="X"
              onclick="return validateSelection() && confirm('선택 항목을 중단하시겠습니까?');">선택항목 중단</button>

      <button type="submit"
              formaction="${pageContext.request.contextPath}/status/bulk.do"
              name="to" value="A"
              onclick="return validateSelection();">선택항목 재개</button>

      <label style="margin-left:12px;">
        <input type="checkbox"
               onchange="location.href='?includeHidden='+(this.checked?'1':'0');"
          <c:if test="${includeHidden}">checked</c:if> /> 중단 포함
      </label>
    </div>

    <table class="table">
      <thead>
        <tr>
          <th><input type="checkbox" onclick="toggleAll(this)"></th>
          <th>상품 ID</th>
          <th>상품명</th>
          <th>카테고리</th>
          <th>가격</th>
          <th>공급업체</th>
          <th>상태(업무)</th>
          <th>표시상태(A/X)</th>
        </tr>
      </thead>
      <tbody>
        <c:choose>
          <c:when test="${not empty productList}">
            <!-- ✅ 행 반복도 JSTL로 -->
            <c:forEach var="p" items="${productList}">
              <tr class="<c:if test='${p.row_status == "X"}'>muted</c:if>">
                <td><input type="checkbox" name="ids" value="${p.product_id}"></td>
                <td>${p.product_id}</td>
                <td>${p.product_name}</td>
                <td>${p.category}</td>
                <td>${p.price}</td>
                <td>${p.supplier_id}</td>

                <!-- ✅ 업무상태 드롭다운: 선택값 반영 -->
                <td>
                  <select name="${p.product_id}">
                    <c:forEach items="${productStatusList}" var="st">
                      <option value="${st}" <c:if test="${st eq p.product_status}">selected</c:if>>${st}</option>
                    </c:forEach>
                  </select>
                </td>

                <!-- 표시상태 배지 -->
                <td>
                  <c:choose>
                    <c:when test="${empty p.row_status || p.row_status == 'A'}">
                      <span class="badge-ok">진행중</span>
                    </c:when>
                    <c:otherwise>
                      <span class="badge-stop">중단</span>
                    </c:otherwise>
                  </c:choose>
                </td>
              </tr>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <tr><td colspan="8">조회된 상품이 없습니다..</td></tr>
          </c:otherwise>
        </c:choose>
      </tbody>
    </table>
  </form>

  <br>
  <a href="${pageContext.request.contextPath}/addProduct.do">상품 등록하기</a>

  <!-- 검색했는데 결과 없을 때 알림 (기존 로직 유지) -->
  <c:if test="${empty productList and (not empty param.product_id or not empty param.product_name or not empty param.category or not empty param.supplier_id)}">
    <script>alert('조회된 상품이 없습니다.');</script>
  </c:if>

</body>
</html>
