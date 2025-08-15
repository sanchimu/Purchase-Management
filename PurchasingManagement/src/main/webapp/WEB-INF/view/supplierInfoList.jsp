<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>공급업체 목록</title>
<style>
    table { width: 80%; border-collapse: collapse; margin: 20px auto; }
    th, td { border: 1px solid #ccc; padding: 8px; text-align: center; }
    th { background-color: #f2f2f2; }
    .btn { padding: 4px 10px; background-color: #4CAF50; color: white; border: none; cursor: pointer; text-decoration: none; }
    .btn:hover { opacity: 0.8; }
    .top-menu { width: 80%; margin: 20px auto; text-align: right; }
    .toolbar { width: 80%; margin: 10px auto; text-align: left; }
    .badge-ok{padding:2px 8px;border-radius:10px;background:#e6f6e6}
    .badge-stop{padding:2px 8px;border-radius:10px;background:#fdeaea}
    .muted{opacity:.55}
</style>
<script>
  function toggleAll(src){
    document.querySelectorAll('input[name="supplier_id"]').forEach(cb => cb.checked = src.checked);
  }
  function getCheckedIds(){
    return Array.from(document.querySelectorAll('input[name="supplier_id"]:checked')).map(cb => cb.value);
  }
  // 상태(A/X) 변경용: 선택한 supplier_id를 ids hidden으로 복제 (StatusBulkHandler 호환)
  function prepStatus(){
    const ids = getCheckedIds();
    if(ids.length === 0){ alert('대상을 선택하세요.'); return false; }
    const box = document.getElementById('idsContainer');
    box.innerHTML = '';
    ids.forEach(v => {
      const h = document.createElement('input');
      h.type = 'hidden'; h.name = 'ids'; h.value = v;
      box.appendChild(h);
    });
    return true;
  }
  // 업무상태 저장용 체크
  function validateSave(){
    const ids = getCheckedIds();
    if(ids.length === 0){ alert('대상을 선택하세요.'); return false; }
    return true;
  }
</script>
</head>
<body>

<div class="top-menu">
    <a href="<c:url value='/insertsupplier.do'/>" class="btn">공급업체 등록</a>
    <a href="<c:url value='/searchsupplier.do'/>" class="btn">공급업체 이름으로 검색</a>
    <a href="javascript:location.reload();" class="btn">새로고침</a>
</div>

<h2 style="text-align:center;">공급업체 목록</h2>

<!-- 한 폼에서: 업무상태 저장 / 표시상태(A/X) 중단·재개 -->
<form method="post">
  <!-- A/X 일괄변경용 공통 파라미터 -->
  <input type="hidden" name="table" value="supplier_info"/>
  <input type="hidden" name="idColumn" value="supplier_id"/>
  <div id="idsContainer"></div>

  <div class="toolbar">
    <!-- ✅ 업무상태 저장 -->
    <button class="btn"
            formaction="${pageContext.request.contextPath}/updatesupplier.do"
            onclick="return validateSave();">선택항목 업무상태 저장</button>

    <!-- 표시상태(A/X) 일괄변경 -->
    <button class="btn"
            formaction="${pageContext.request.contextPath}/status/bulk.do"
            name="to" value="X"
            onclick="return prepStatus() && confirm('선택 항목을 중단하시겠습니까?');">선택항목 중단</button>

    <button class="btn"
            formaction="${pageContext.request.contextPath}/status/bulk.do"
            name="to" value="A"
            onclick="return prepStatus();">선택항목 재개</button>

    <!-- 중단 포함 토글 -->
    <label style="margin-left:12px;">
      <input type="checkbox"
             onchange="location.href='?includeHidden='+(this.checked?'1':'0');"
        <c:if test="${param.includeHidden=='1' || includeHidden}">checked</c:if> /> 중단 포함
    </label>
  </div>

  <!-- 상태 드롭다운 기본 리스트(핸들러에서 안 주면 JSP가 생성) -->
  <c:if test="${empty supplierStatusList}">
    <c:set var="supplierStatusList" value="${fn:split('활성,거래 중지,폐업,휴면', ',')}"/>
  </c:if>

  <table>
    <tr>
        <th><input type="checkbox" onclick="toggleAll(this)"></th>
        <th>공급업체 ID</th>
        <th>공급업체 이름</th>
        <th>연락처</th>
        <th>주소</th>
        <th>상태(업무)</th>
        <th>표시상태(A/X)</th>
    </tr>

    <c:forEach var="supplier" items="${supplierList}">
      <!-- 기본은 A만, '중단 포함'이면 X도 표시 -->
      <c:if test="${param.includeHidden=='1' || includeHidden || empty supplier.row_status || supplier.row_status=='A'}">
        <tr class="<c:if test='${supplier.row_status=="X"}'>muted</c:if>">
            <!-- 체크박스(업무상태 저장/표시상태 변경 공용) -->
            <td><input type="checkbox" name="supplier_id" value="${supplier.supplier_id}"></td>
            <td>${supplier.supplier_id}</td>
            <td>${supplier.supplier_name}</td>
            <td>${supplier.contact_number}</td>
            <td>${supplier.address}</td>

            <!-- 업무상태 드롭다운: name=공급업체ID -->
            <td>
              <select name="${supplier.supplier_id}">
                <c:forEach var="st" items="${supplierStatusList}">
                  <option value="${st}" <c:if test="${st eq supplier.supplier_status}">selected</c:if>>${st}</option>
                </c:forEach>
              </select>
            </td>

            <td>
              <c:choose>
                <c:when test="${empty supplier.row_status || supplier.row_status=='A'}"><span class="badge-ok">진행중</span></c:when>
                <c:otherwise><span class="badge-stop">중단</span></c:otherwise>
              </c:choose>
            </td>
        </tr>
      </c:if>
    </c:forEach>

    <c:if test="${empty supplierList}">
        <tr><td colspan="7">등록된 공급업체가 없습니다.</td></tr>
    </c:if>
  </table>
</form>

</body>
</html>
