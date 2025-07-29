<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>상품 추가</title>
</head>
<body>
<h1>상품 추가</h1>

<form action = "addProduct.do" method="post">

<p>
	상품명 : <br/><input type = "text" name="product_name" value="${param.product_name}">
</p>
<p>
	상품명 : <br/><input type = "text" name="category" value="${param.category}">
</p>
<p>
	상품명 : <br/><input type = "text" name="price" value="${param.price}">
</p>
<p>
	상품명 : <br/><input type = "text" name="supplier_id" value="${param.supplier_id}">
</p>
<p>
	<input type = "submit" value="상품 추가">
</p>
</body>
</html>