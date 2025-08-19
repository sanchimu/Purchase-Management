<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>상품 추가</title>
</head>
<body>
<h1>상품 추가</h1>

<c:if test="${success}">
  <script>
    alert('상품 추가 완료');
    document.addEventListener('DOMContentLoaded', function(){
      const f = document.getElementById('addForm');
      if (f) f.reset();
    });
  </script>
</c:if>

<c:if test="${not empty errors}">
  <div style="color:#c00; margin:8px 0;">입력값을 확인하세요.</div>
</c:if>

<form id="addForm" action="<c:url value='/addProduct.do'/>" method="post">
  <p>
    상품명<br/>
    <input type="text" name="product_name" required value="<c:out value='${form_product_name}'/>">
    <c:if test="${errors.product_name != null}"><span style="color:#c00;">필수</span></c:if>
  </p>

  <p>
    카테고리<br/>
    <!-- 선택 + 자유입력 -->
    <input type="text" name="category" list="categoryOptions" required value="<c:out value='${form_category}'/>">
    <datalist id="categoryOptions">
      <c:forEach var="c" items="${categories}">
        <option value="<c:out value='${c}'/>" />
      </c:forEach>
    </datalist>
    <c:if test="${errors.category != null}"><span style="color:#c00;">필수</span></c:if>
  </p>

  <p>
    가격<br/>
    <input type="text" name="price" value="<c:out value='${form_price}'/>">
    <c:if test="${errors.price != null}"><span style="color:#c00;">숫자</span></c:if>
  </p>

  <p>
    공급업체<br/>
    <select name="supplier_id" required>
      <option value="">공급업체 선택</option>
      <c:forEach var="s" items="${supplierList}">
        <option value="<c:out value='${s.supplier_id}'/>"
                <c:if test="${form_supplier_id eq s.supplier_id}">selected</c:if>>
          <c:out value='${s.supplier_name}'/> (<c:out value='${s.supplier_id}'/>)
        </option>
      </c:forEach>
    </select>
    <c:if test="${errors.supplier_id != null}"><span style="color:#c00;">필수</span></c:if>
  </p>

  <p>
    업무상태<br/>
    <select name="product_status">
      <c:forEach var="st" items="${productStatusList}">
        <option value="<c:out value='${st}'/>"
          <c:if test="${(not empty form_product_status and form_product_status eq st) or (empty form_product_status and st eq '정상판매')}">selected</c:if>>
          <c:out value='${st}'/>
        </option>
      </c:forEach>
    </select>
  </p>

  <p>
    <input type="submit" value="상품 추가">
    <button type="button" onclick="location.href='<c:url value="/listProducts.do"/>'">뒤로 가기</button>
  </p>
</form>

</body>
</html>
