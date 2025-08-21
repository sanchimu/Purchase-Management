<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>仕入先 修正</title>
<style>
  .wrap{width:600px;margin:30px auto;font-family:sans-serif}
  .form-row{margin-bottom:12px}
  label{display:block;margin-bottom:4px;font-weight:bold}
  input[type=text], select{width:100%;padding:8px;border:1px solid #ccc;border-radius:4px}
  .actions{margin-top:16px;display:flex;gap:8px}
  .btn{padding:8px 14px;background:#4CAF50;color:#fff;border:none;border-radius:4px;cursor:pointer}
  .btn.secondary{background:#777}
</style>
</head>
<body>
<div class="wrap">
  <h2>仕入先 修正</h2>

  <form method="post" action="<c:url value='/updatesupplier.do'/>">
    <!-- PKはreadonly + hiddenで送信 -->
    <div class="form-row">
      <label>仕入先ID</label>
      <input type="text" value="${supplier.supplier_id}" readonly>
      <input type="hidden" name="supplier_id" value="${supplier.supplier_id}">
    </div>

    <div class="form-row">
      <label>仕入先名</label>
      <input type="text" name="supplier_name" value="${supplier.supplier_name}" required>
    </div>

    <div class="form-row">
      <label>連絡先</label>
      <input type="text" name="contact_number" value="${supplier.contact_number}">
    </div>

    <div class="form-row">
      <label>住所</label>
      <input type="text" name="address" value="${supplier.address}">
    </div>

    <div class="form-row">
      <label>業務状態</label>
      <select name="supplier_status" required>
        <c:set var="statusList" value="${empty supplierStatusList ? 
              '有効,取引停止,廃業,休眠' : supplierStatusList}" />
        <c:forEach var="st" items="${statusList}">
          <option value="${st}" <c:if test="${st eq supplier.supplier_status}">selected</c:if>>${st}</option>
        </c:forEach>
      </select>
    </div>

    <div class="form-row">
      <label>表示状態 (A/X)</label>
      <select name="row_status">
        <option value="A" <c:if test="${empty supplier.row_status || supplier.row_status == 'A'}">selected</c:if>>A (表示)</option>
        <option value="X" <c:if test="${supplier.row_status == 'X'}">selected</c:if>>X (中断)</option>
      </select>
    </div>

    <div class="actions">
      <button class="btn" type="submit">保存</button>
      <a class="btn secondary" href="<c:url value='/listsupplier.do'/>">一覧に戻る</a>
    </div>
  </form>
</div>
</body>
</html>
