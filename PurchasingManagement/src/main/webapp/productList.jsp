<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.purchase.vo.Product" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>상품 목록</title>
<script>
// 폼 제출 전 체크박스 선택 여부 확인
function validateForm() {
    const checkboxes = document.querySelectorAll('input[name="productIds"]:checked');
    if (checkboxes.length === 0) {
        alert("삭제할 상품을 선택하세요.");
        return false; // 제출 막기
    }
    return true; // 제출 허용
}
</script>

</head>
<body>
    <h2>상품 목록</h2>
     <form action="deleteProducts.do" method="post" onsubmit="return validateForm();">
        <table border="1">
            <tr>
                <th>선택</th>
                <th>상품 ID</th>
                <th>상품명</th>
                <th>카테고리</th>
                <th>가격</th>
                <th>공급업체</th>
            </tr>
            <%
                List<Product> productList = (List<Product>) request.getAttribute("productList");
                for (Product p : productList) {
            %>
            <tr>
                <td><input type="checkbox" name="productIds" value="<%= p.getProduct_id() %>"></td>
                <td><%= p.getProduct_id() %></td>
                <td><%= p.getProduct_name() %></td>
                <td><%= p.getCategory() %></td>
                <td><%= p.getPrice() %></td>
                <td><%= p.getSupplier_id() %></td>
            </tr>
            <% } %>
        </table>
        <br>
        <input type="submit" value="선택 항목 삭제">
    </form>
       <br>
    <a href="addProduct.do">상품 등록하기</a>
</body>
</html>