<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>商品追加</title>
<style>
/* 전체 레이아웃 */
body {
    font-family: Arial, sans-serif;
}

/* 제목 중앙 정렬 */
h1 {
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

form input[type="text"],
form select {
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
<script>
window.onload = function() {
    // 서버에서 전달된 success 플래그가 true일 경우 alert 띄우기
    <% if (request.getAttribute("noPriceError") != null){ %>
    	alert("価格情報がありません。");
    	<%}%>
}
</script>
</head>
<body>

<h1>商品追加</h1>

<form action="addProduct.do" method="post"> <!-- submit 시 실행 handler와 데이터 전달 방식 -->
    <p>
        商品名 :<br/>
        <input type="text" name="product_name">
    </p>
    <p>
        分類 :<br/>
        <input type="text" name="category">
    </p>
    <p>
        価格 :<br/>
        <input type="text" name="price">
    </p>
    <p>
        仕入先ID :<br/>
        <select name="supplier_id">
            <option value="">-- 選択 --</option> <!-- 조회 때와 마찬가지로 supplierList 값을 반복해서 foreach로 체크하며 sup에 suppliser_id를 넣고 드롭박스에 값 생성 -->
            <c:forEach items="${supplierList}" var="sup">
                <option value="${sup}" <c:if test="${product.supplier_id == sup}">selected</c:if>>
                    ${sup}
                </option>
            </c:forEach>
        </select>
    </p>
    <p>
        <input type="submit" value="商品追加">
        <input type="button" value="戻る" onclick="location.href='listProducts.do'"><!-- 버튼 두개를 넣기위해 submit이 아닌 button으로 type을 정함 -->
    </p>
</form>

</body>
</html>