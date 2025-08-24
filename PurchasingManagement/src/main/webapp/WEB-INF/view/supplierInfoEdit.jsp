<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>仕入先 修正</title>
<style>
  /* 레이아웃 및 스타일 */
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

  <!-- 공급업체 수정 폼. 제출 시 /updatesupplier.do 로 POST 요청 -->
  <form method="post" action="<c:url value='/updatesupplier.do'/>">

    <!-- PK: 화면에는 표시(readonly), 전송은 hidden 으로 -->
    <div class="form-row">
      <label>仕入先ID</label>
      <input type="text" value="${supplier.supplier_id}" readonly>
      <input type="hidden" name="supplier_id" value="${supplier.supplier_id}">
    </div>

    <!-- 공급업체명 입력 -->
    <div class="form-row">
      <label>仕入先名</label>
      <input type="text" name="supplier_name" value="${supplier.supplier_name}" required>
    </div>

    <!-- 연락처 입력 -->
    <div class="form-row">
      <label>連絡先</label>
      <input type="text" name="contact_number" value="${supplier.contact_number}">
    </div>

    <!-- 주소 입력 -->
    <div class="form-row">
      <label>住所</label>
      <input type="text" name="address" value="${supplier.address}">
    </div>

    <!-- 거래 상태 선택 (유효/중단/폐업/휴면) -->
    <div class="form-row">
      <label>業務状態</label>
      <select name="supplier_status" required>
        <!-- 상태 목록이 없으면 기본값 4개 문자열 사용 -->
        <c:set var="statusList" value="${empty supplierStatusList ? 
              '有効,取引停止,廃業,休眠' : supplierStatusList}" />
        <!-- 상태 목록을 순회하며 option 생성 -->
        <c:forEach var="st" items="${statusList}">
          <option value="${st}" <c:if test="${st eq supplier.supplier_status}">selected</c:if>>${st}</option>
        </c:forEach>
      </select>
    </div>

    <!-- 표시 상태 (A=표시, X=중단) -->
    <div class="form-row">
      <label>表示状態 (A/X)</label>
      <select name="row_status">
        <option value="A" <c:if test="${empty supplier.row_status || supplier.row_status == 'A'}">selected</c:if>>A (表示)</option>
        <option value="X" <c:if test="${supplier.row_status == 'X'}">selected</c:if>>X (中断)</option>
      </select>
    </div>

    <!-- 버튼 영역: 저장 / 목록으로 돌아가기 -->
    <div class="actions">
      <button class="btn" type="submit">保存</button>
      <a class="btn secondary" href="<c:url value='/listsupplier.do'/>">一覧に戻る</a>
    </div>
  </form>
</div>
</body>
</html>
