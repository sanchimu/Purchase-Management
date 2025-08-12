<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>구매요청 등록</title>
</head>
<body>

<h2>구매요청 등록</h2>
<form action="<%=request.getContextPath()%>/insertpurchaserequest.do" method="post">
    상품ID: <input type="text" name="product_id" required><br>
    수량: <input type="number" name="quantity" required><br>
    요청자: <input type="text" name="requester_name" required><br>
    <input type="submit" value="등록">
    <button type="button" onclick="location.href='<%=request.getContextPath()%>/listpurchaserequest.do'">목록</button>
</form>

</body>
</html>