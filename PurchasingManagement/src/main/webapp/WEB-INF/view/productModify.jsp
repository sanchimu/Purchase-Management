<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>商品情報修正</title>
<style>
/* 전체 레이아웃 */
body {
    font-family: Arial, sans-serif;
}

/* 제목 중앙 정렬 */
h2 {
    text-align: center;
}

/* 폼 스타일 */
form {
    width: 50%;
    margin: 20px auto;
    padding: 15px;
    border: 1px solid #ccc;
    border-radius: 8px;
    background-color: #f9f9f9;
}

/* 입력 필드 */
form p {
    margin-bottom: 15px;
}

form input[type="text"] {
    width: 100%;
    padding: 6px 8px;
    margin-top: 4px;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-sizing: border-box;
}

/* 버튼 스타일 */
form input[type="submit"],
form input[type="button"] {
    padding: 6px 12px;
    margin-right: 10px;
    background-color: #4CAF50;
    border: none;
    border-radius: 4px;
    color: white;
    cursor: pointer;
    text-decoration: none;
}

form input[type="submit"]:hover,
form input[type="button"]:hover {
    opacity: 0.85;
}
</style>
</head>
<body>

<h2>商品情報修正</h2>

<form action="modifyProduct.do" method="post"> <!-- submit 시 실행될 handler와 데이터 전달 방식 -->
    <p>
        商品名 :<br/>
        <input type="text" name="product_name" value="${product.product_name}">
    </p>
    <p>
        分類 :<br/>
        <input type="text" name="category" value="${product.category}">
    </p>
    <p>
        価格 :<br/>
        <input type="text" name="price" value="${product.price}">
    </p>
    <input type="hidden" name="product_id" value="${product.product_id}"> <!-- 표시는 되지 않지만 수정을 위해 필요한 값들을 받기 위해 hidden으로 type을 세팅 후 값을 부여 -->
    <input type="hidden" name="supplier_id" value="${product.supplier_id}">
    <p>
        <input type="submit" value="修正完了">
        <input type="button" value="戻る" onclick="location.href='listProducts.do'"><!-- 버튼 두개를 넣기위해 submit이 아닌 button으로 type을 정함 -->
    </p>
</form>

</body>
</html>