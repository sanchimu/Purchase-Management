<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<h2>상품 정보 수정</h2>
<form action = "modifyProduct.do" method ="post">
	상품명 : <input type = "text" name = "product_name" value="${product.product_name}"><br>
	카테고리 : <input type = "text" name = "category" value="${product.category}"><br>
	가격 : <input type = "text" name = "price" value="${product.price}"><br>
	 <input type="hidden" name="product_id" value="${product.product_id}"><!-- WHERE product_id = ? 조건을 쓰고 있는데, 폼에 product_id를 hidden으로 넣지 않으면 POST 요청 시 req.getParameter("product_id") 값이 null이라 update가 안됨 -->
	<input type="submit" value="수정 완료">
</form>

<input type="button" value="뒤로 가기" onclick="location.href='listProducts.do'">
</body>
</html>