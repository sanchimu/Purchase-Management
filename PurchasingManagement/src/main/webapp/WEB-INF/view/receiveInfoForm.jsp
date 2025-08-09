<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>입고 등록</title>
</head>
<body>
<h2>입고 등록</h2>

<form action="${pageContext.request.contextPath}/listReceiveInfos.do" method="post">
    주문 ID: <input type="text" name="order_id" required><br>
    상품 ID: <input type="text" name="product_id" required><br>
    수량: <input type="number" name="quantity" required><br>
    입고일자: <input type="date" name="receive_date"><br>
    <input type="submit" value="등록">
</form>
</body>
</html>
