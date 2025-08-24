<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>仕入先一覧</title>
<style>
/* 테이블 및 버튼 스타일 */
table { width: 80%; border-collapse: collapse; margin: 20px auto; }
th, td { border: 1px solid #ccc; padding: 8px; text-align: center; }
th { background-color: #f2f2f2; }
.btn { padding: 4px 10px; background-color: #4CAF50; color: white; border: none; cursor: pointer; text-decoration: none; }
.btn:hover { opacity: 0.8; }
.top-menu { width: 80%; margin: 20px auto; text-align: right; }
.toolbar { width: 80%; margin: 10px auto; text-align: left; }
.badge-ok { padding: 2px 8px; border-radius: 10px; background: #e6f6e6 }
.badge-stop { padding: 2px 8px; border-radius: 10px; background: #fdeaea }
.muted { opacity: .55 }
</style>

<script>
/* 전체 체크박스 토글 */
function toggleAll(src){
  document.querySelectorAll('input[name="supplier_id"]').forEach(cb => cb.checked = src.checked);
}
/* 체크된 ID 배열로 수집 */
function getCheckedIds(){
  return Array.from(document.querySelectorAll('input[name="supplier_id"]:checked')).map(cb => cb.value);
}
/* 선택된 항목을 hidden input으로 form에 담기 */
function prepStatus(){
  const ids = getCheckedIds();
  if(ids.length === 0){ alert('対象を選択してください。'); return false; }
  const box = document.getElementById('idsContainer');
  box.innerHTML = '';
  ids.forEach(v => {
    const h = document.createElement('input');
    h.type = 'hidden'; h.name = 'ids'; h.value = v;
    box.appendChild(h);
  });
  return true;
}
/* 저장 버튼 클릭 시 선택 여부 확인 */
function validateSave(){
  const ids = getCheckedIds();
  if(ids.length === 0){ alert('対象を選択してください。'); return false; }
  return true;
}
</script>
</head>
<body>

<!-- 상단 메뉴: 등록, 새로고침 -->
<div class="top-menu">
  <a href="<c:url value='/insertsupplier.do'/>" class="btn">仕入先登録</a>
  <a href="javascript:location.reload();" class="btn">再読み込み</a>
</div>

<h2 style="text-align: center;">仕入先一覧</h2>

<!-- 검색폼: 공급업체명으로 검색 -->
<form action="<c:url value='/listsupplier.do'/>" method="get" style="text-align:center; margin-bottom: 20px;">
  仕入先名：
  <input type="text" name="supplier_name" value="${param.supplier_name}" />
  <input type="submit" class="btn" value="検索" />
</form>

<!-- 상태 변경 및 리스트 테이블 -->
<form method="post">
  <input type="hidden" name="table" value="supplier_info" />
  <input type="hidden" name="idColumn" value="supplier_id" />
  <div id="idsContainer"></div>

  <!-- 툴바: 상태 저장/중단/재개/중단포함 체크 -->
  <div class="toolbar">
    <button class="btn" formaction="${pageContext.request.contextPath}/updatestatus.do" onclick="return validateSave();">選択項目 業務状態保存</button>
    <button class="btn" formaction="${pageContext.request.contextPath}/status/bulk.do" name="to" value="X" onclick="return prepStatus() && confirm('選択項目を中断しますか？');">選択項目 中断</button>
    <button class="btn" formaction="${pageContext.request.contextPath}/status/bulk.do" name="to" value="A" onclick="return prepStatus();">選択項目 再開</button>
    <label style="margin-left: 12px;">
      <input type="checkbox" onchange="location.href='?includeHidden='+(this.checked?'1':'0');" 
             <c:if test="${param.includeHidden=='1' || includeHidden}">checked</c:if> />
      中断を含む
    </label>
  </div>

  <!-- 상태값 리스트 준비 (없으면 기본값 사용) -->
  <c:if test="${empty supplierStatusList}">
    <c:set var="supplierStatusList" value="${fn:split('有効,取引停止,廃業,休眠', ',')}" />
  </c:if>

  <!-- 공급업체 테이블 -->
  <table>
    <tr>
      <th><input type="checkbox" onclick="toggleAll(this)"></th>
      <th>仕入先ID</th>
      <th>仕入先名</th>
      <th>連絡先</th>
      <th>住所</th>
      <th>状態(業務)</th>
      <th>表示状態(A/X)</th>
    </tr>

    <!-- 공급업체 리스트 출력 -->
    <c:forEach var="supplier" items="${supplierList}">
      <!-- includeHidden 이 체크되었거나 row_status=='A' 인 경우만 표시 -->
      <c:if test="${param.includeHidden=='1' || includeHidden || empty supplier.row_status || supplier.row_status=='A'}">
        <tr class="<c:if test='${supplier.row_status=="X"}'>muted</c:if>">
          <td><input type="checkbox" name="supplier_id" value="${supplier.supplier_id}"></td>
          <!-- ID 클릭 시 수정 페이지로 이동 -->
          <td>
            <a href="<c:url value='/editsupplier.do'><c:param name='supplier_id' value='${supplier.supplier_id}'/></c:url>">
              ${supplier.supplier_id}
            </a>
          </td>
          <td>${supplier.supplier_name}</td>
          <td>${supplier.contact_number}</td>
          <td>${supplier.address}</td>
          <!-- 상태 선택 박스 -->
          <td>
            <select name="${supplier.supplier_id}">
              <c:forEach var="st" items="${supplierStatusList}">
                <option value="${st}" <c:if test="${st eq supplier.supplier_status}">selected</c:if>>${st}</option>
              </c:forEach>
            </select>
          </td>
          <!-- 표시 상태 뱃지 -->
          <td>
            <c:choose>
              <c:when test="${empty supplier.row_status || supplier.row_status=='A'}">
                <span class="badge-ok">進行中</span>
              </c:when>
              <c:otherwise>
                <span class="badge-stop">中断</span>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
      </c:if>
    </c:forEach>

    <!-- 리스트가 비었을 경우 메시지 -->
    <c:if test="${empty supplierList}">
      <tr>
        <td colspan="7">登録された仕入先はありません。</td>
      </tr>
    </c:if>
  </table>
</form>

</body>
</html>
