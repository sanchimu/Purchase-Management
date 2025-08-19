<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>상품 수정</h1>

<form action="<c:url value='/updateProducts.do'/>" method="post">
  <input type="hidden" name="product_id" value="${p.product_id}" />
  <input type="hidden" name="includeHidden" value="${includeHidden}" />

  <table>
    <tr>
      <th>상품 ID</th>
      <td>${p.product_id}</td>
    </tr>

    <tr>
      <th>상품명</th>
      <td>
        <input type="text" name="product_name" required value="${p.product_name}" />
        <c:if test="${errors.product_name != null}"><span style="color:#c00">필수</span></c:if>
      </td>
    </tr>

    <tr>
      <th>카테고리</th>
      <td>
        <!-- 기존 목록(datalist) + 신규입력 허용 -->
        <input type="text" name="category" list="categoryOptions" required value="${p.category}">
        <datalist id="categoryOptions">
          <c:forEach var="c" items="${categories}">
            <option value="${c}" />
          </c:forEach>
        </datalist>
        <c:if test="${errors.category != null}"><span style="color:#c00">필수</span></c:if>
      </td>
    </tr>

    <tr>
      <th>가격</th>
      <td>
        <input type="text" name="price" value="${p.price}" />
        <c:if test="${errors.price != null}"><span style="color:#c00">숫자</span></c:if>
      </td>
    </tr>

    <tr>
      <th>공급업체</th>
      <td>
        <select name="supplier_id" required>
          <option value="">공급업체 선택</option>
          <c:forEach var="s" items="${supplierList}">
            <option value="${s.supplier_id}" <c:if test="${p.supplier_id == s.supplier_id}">selected</c:if>>
              ${s.supplier_name} (${s.supplier_id})
            </option>
          </c:forEach>
        </select>
        <c:if test="${errors.supplier_id != null}"><span style="color:#c00">필수</span></c:if>
      </td>
    </tr>

    <tr>
      <th>업무상태</th>
      <td>
        <select name="product_status">
          <c:forEach var="st" items="${productStatusList}">
            <option value="${st}" <c:if test="${p.product_status == st}">selected</c:if>>${st}</option>
          </c:forEach>
        </select>
      </td>
    </tr>
  </table>

  <button type="submit">수정 저장</button>
  <a href="<c:url value='/listProducts.do'>
             <c:param name='includeHidden' value='${includeHidden}'/>
           </c:url>">목록</a>
</form>
