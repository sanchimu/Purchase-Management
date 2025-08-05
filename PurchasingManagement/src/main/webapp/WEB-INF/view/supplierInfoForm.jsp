<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>공급업체 등록</title>
<style>
    body { font-family: Arial, sans-serif; margin: 20px; }
    h2 { color: #333; }
    form { width: 400px; }
    label { display: block; margin-top: 10px; }
    input[type="text"] {
        width: 100%;
        padding: 8px;
        margin-top: 4px;
        box-sizing: border-box;
    }
    .btn-area {
        margin-top: 15px;
    }
    input[type="submit"], input[type="button"] {
        padding: 8px 15px;
        border: none;
        cursor: pointer;
        margin-right: 5px;
    }
    input[type="submit"] {
        background-color: #4CAF50;
        color: white;
    }
    input[type="button"] {
        background-color: #ccc;
    }
</style>
</head>
<body>

<h2>공급업체 등록</h2>

<form action="<%=request.getContextPath()%>/insertsupplier.do" method="post">
    <label for="supplier_id">공급업체 ID</label>
    <input type="text" id="supplier_id" name="supplier_id" required>

    <label for="supplier_name">공급업체명</label>
    <input type="text" id="supplier_name" name="supplier_name" required>

    <label for="contact_number">연락처</label>
    <input type="text" id="contact_number" name="contact_number">

    <label for="address">주소</label>
    <input type="text" id="address" name="address">

    <div class="btn-area">
        <input type="submit" value="등록">
        <input type="button" value="목록으로" onclick="location.href='<%=request.getContextPath()%>/listsupplier.do'">
    </div>
</form>

</body>
</html>
