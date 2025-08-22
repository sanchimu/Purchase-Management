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
function toggleAll(src){
  document.querySelectorAll('input[name="supplier_id"]').forEach(cb => cb.checked = src.checked);
}
function getCheckedIds(){
  return Array.from(document.querySelectorAll('input[name="supplier_id"]:checked')).map(cb => cb.value);
}
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
function validateSave(){
  const ids = getCheckedIds();
  if(ids.length === 0){ alert('対象を選択してください。'); return false; }
  return true;
}
</script>
</head>
<body>

<div class="top-menu">
  <a href="<c:url value='/insertsupplier.do'/>" class="btn">仕入先登録</a>

  <a href="javascript:location.reload();" class="btn">再読み込み</a>
</div>

<h2 style="text-align: center;">仕入先一覧</h2>


<form action="<c:url value='/listsupplier.do'/>" method="get" style="text-align:center; margin-bottom: 20px;">
  仕入先名：
  <input type="text" name="supplier_name" value="${param.supplier_name}" />
  <input type="submit" class="btn" value="検索" />
</form>

<form method="post">
  <input type="hidden" name="table" value="supplier_info" />
  <input type="hidden" name="idColumn" value="supplier_id" />
  <div id="idsContainer"></div>

  <div class="toolbar">
    <button class="btn" formaction="${pageContext.request.contextPath}/updatestatus.do" onclick="return validateSave();">選択項目 業務状態保存</button>
    <button class="btn" formaction="${pageContext.request.contextPath}/status/bulk.do" name="to" value="X" onclick="return prepStatus() && confirm('選択項目を中断しますか？');">選択項目 中断</button>
    <button class="btn" formaction="${pageContext.request.contextPath}/status/bulk.do" name="to" value="A" onclick="return prepStatus();">選択項目 再開</button>
    <label style="margin-left: 12px;">
      <input type="checkbox" onchange="location.href='?includeHidden='+(this.checked?'1':'0');" <c:if test="${param.includeHidden=='1' || includeHidden}">checked</c:if> />
      中断を含む
    </label>
  </div>

  <c:if test="${empty supplierStatusList}">
    <c:set var="supplierStatusList" value="${fn:split('有効,取引停止,廃業,休眠', ',')}" />
  </c:if>

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

    <c:forEach var="supplier" items="${supplierList}">
      <c:if test="${param.includeHidden=='1' || includeHidden || empty supplier.row_status || supplier.row_status=='A'}">
        <tr class="<c:if test='${supplier.row_status=="X"}'>muted</c:if>">
          <td><input type="checkbox" name="supplier_id" value="${supplier.supplier_id}"></td>
          <td><a href="<c:url value='/editsupplier.do'><c:param name='supplier_id' value='${supplier.supplier_id}'/></c:url>">${supplier.supplier_id}</a></td>
          <td>${supplier.supplier_name}</td>
          <td>${supplier.contact_number}</td>
          <td>${supplier.address}</td>
          <td>
            <select name="${supplier.supplier_id}">
              <c:forEach var="st" items="${supplierStatusList}">
                <option value="${st}" <c:if test="${st eq supplier.supplier_status}">selected</c:if>>${st}</option>
              </c:forEach>
            </select>
          </td>
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

    <c:if test="${empty supplierList}">
      <tr>
        <td colspan="7">登録された仕入先はありません。</td>
      </tr>
    </c:if>
  </table>
</form>

</body>
</html>
