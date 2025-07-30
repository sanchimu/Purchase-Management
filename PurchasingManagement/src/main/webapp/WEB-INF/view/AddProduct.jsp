<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script>
window.onload = function() {
    // 서버에서 전달된 success 플래그가 true일 경우 alert 띄우기
    <% if (request.getAttribute("success") != null) { %>
        alert("상품 추가 완료");
        // 입력 필드 초기화 (방법 1: form 전체 reset)
        document.querySelector("form").reset();
    <% } %>
}
</script>
<title>상품 추가</title>
</head>
<body>
<h1>상품 추가</h1>

<form action = "addProduct.do" method="post">

<p>
	상품명 : <br/><input type = "text" name="product_name" <%-- value="${param.product_name}" --%>>
</p>
<p>
	카테고리 : <br/><input type = "text" name="category" <%-- <%-- value="${param.category}" --%>>
</p>
<p>
	가격 : <br/><input type = "text" name="price" <%-- value="${param.price}" --%>>
</p>
<p>
	공급업체 ID  : <br/><input type = "text" name="supplier_id" <%-- value="${param.supplier_id}" --%>>
</p>
<p>
	<input type = "submit" value="상품 추가">
	<input type="button" value="뒤로 가기" onclick="location.href='listProducts.do'">
</p>
</body>
</html>