<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>공급업체 등록</title>
<style>  /* 디자인 지정 */
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
<body> <!-- 화면에 표시될 본문  -->

<h2>공급업체 등록</h2>

<form action="<%=request.getContextPath()%>/insertsupplier.do" method="post">
<%-- form 태그 : 웹 브라우저에서 사용자가 입력한 데이터를 서버로 보내기 위해 사용하는 태그
여기서는 insertsupplier.do 로 POST 방식으로 데이터 보냄
<%=request.getContextPath()%> 는 <%= %>안에 적힌 코드의 실행결과를 HTML에 출력
request.getContextPath는 현재 웹 애플리케이션의 컨텍스트 경로 반환. 여기서는 /PurchasingManagement 반환 --%>
    <label for="supplier_id">공급업체 ID</label>
<%--label 태그 : 해당 입력 필드의 설명을 붙이는 태그
for="supplier_id": 이 label이 어느 input과 연결되는지 지정 
"공급업체 ID"글자를 눌러도 입력창이 선택됨--%>
    <input type="text" id="supplier_id" name="supplier_id" required>
<%--HTML 입력 필드를 만듦. type="text" : 한 줄 텍스트 입력 필드. 사용자가 문자열 입력
id="supplier_id" HTML 문서 내에서 이 input을 식별하는 이름.
name="supplier_id" 서버로 전송할 때의 파라미터 이름. 서버에서 name값을 사용해서 데이터 읽음
required : 사용자가 입력하지 않고 제출하려고 하면 브라우저에서 경고창 띄움 --%>

	<%--공급업체명 입력창, 필수입력 --%>
    <label for="supplier_name">공급업체명</label>
    <input type="text" id="supplier_name" name="supplier_name" required>
	<%--연락처 입력창, 필수입력X --%>
    <label for="contact_number">연락처</label>
    <input type="text" id="contact_number" name="contact_number">
	<%--주소 입력창, 필수입력X --%>
    <label for="address">주소</label>
    <input type="text" id="address" name="address">

    <div class="btn-area">
        <input type="submit" value="등록">
        <%--type="submit" : form 안에서 버튼 클릭하면 폼 데이터가 서버로 전송됨. 
        value="등록" 버튼에 표시될 글자  --%>
        <input type="button" value="목록으로" onclick="location.href='<%=request.getContextPath()%>/listsupplier.do'">
        <%--type="button" : 일반 버튼. 폼 전송 기능X 
        onclick="location.href='...'" 버튼 클릭 시 실행할 코드. 브라우저의 주소를 지정된 값으로 변경--%>
    </div>
</form>

</body>
</html>
