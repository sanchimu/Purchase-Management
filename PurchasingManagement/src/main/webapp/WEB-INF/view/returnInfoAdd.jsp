<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.purchase.vo.ReceiveInfo" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>返品要請</title>
<style>
body {
    font-family: Arial, sans-serif;
    text-align: center;  /* form 박스를 화면 중앙에 정렬 */
}


/* 제목 중앙 정렬 */
h3 {
    text-align: center;
}

/* 테이블 스타일 */
table {
    border-collapse: collapse;
    margin: 10px auto;       /* 폼 안에서 중앙 */
    width: auto;             /* 내용에 맞춰 유동 */
}
th, td {
    border: 1px solid #ccc;
    padding: 8px;
    text-align: center;
    white-space: nowrap; /* 줄바꿈 없이 글자 크기에 맞춤 */
}

th {
    background-color: #f2f2f2;
}

/* 폼 스타일 */
form {
    display: inline-block;   /* 내부 크기에 맞춰 유동 */
    margin: 20px auto;
    padding: 15px;
    border: 1px solid #ccc;
    border-radius: 8px;
    background-color: #f9f9f9;
    text-align: left;        /* 폼 내부 기본은 왼쪽 정렬 */
}


/* 입력 필드 */
input[type="text"] {
    width: 100%;
    padding: 6px 8px;
    margin-top: 4px;
    margin-bottom: 10px;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-sizing: border-box;
}

/* 버튼 스타일 */
input[type="submit"],
input[type="button"] {
    padding: 6px 12px;
    margin-right: 10px;
    background-color: #4CAF50;
    border: none;
    border-radius: 4px;
    color: white;
    cursor: pointer;
}

input[type="submit"]:hover,
input[type="button"]:hover {
    opacity: 0.85;
}
</style>
<script>
window.onload = function() { //경고창을 띄우기 위한 함수 (警告ウィンドウを出すための関数)
    <% if (request.getAttribute("success") != null) { %> //success값이 있을경우 경고창 발생 (alert) (success値がある場合、警告ウィンドウが発生(alert))
        alert("返品要請完了");
        document.querySelector("form").reset();
        <% } %>
        //아래는 error뜰때 키값 가져와서 경고창 띄우는건데 productList에는 이런거 안쓰고 여기만 쓰는 이유는
        //returnInfoAdd는 특이하게 갱신때마다 입고 리스트를 가져와서 갱신해줘야함 그래서 redirect가 불가피했기 때문에 (현재 내 수준에서) 해당 부분을 참고하여 사용
        const urlParams = new URLSearchParams(window.location.search); 	//URLSearchParams는 괄호안의 쿼리스트링을 파싱해줌 /  현재 페이지의 URL 쿼리스트링을 찾음 (Returnprocesshandler의 ?error" + errorMsg)
        																//URL Search Paramsは括弧内のクエリーストリングを解析する / 現在ページのURLクエリーストリングを探す (Returnprocesshandlerの?error" + error Msg)
        const error = urlParams.get("error"); // error의 파라미터 값을 가져옴 (errorのパラメータ値を取得)
        if (error) {
            alert(error);
        }
        
        const form = document.querySelector("form");
        form.addEventListener("submit", function(e) {
            const selected = document.querySelector("input[name='receiveId']:checked");
            if (!selected) return; // 선택 안 하면 Handler에서 alert 처리

            const receiveId = selected.value;
            const qty = document.querySelector("input[name='quantity']").value.trim();
            const available = document.querySelector("input[name='available_" + receiveId + "']").value;

            if (qty !== "" && !isNaN(qty) && parseInt(qty, 10) > parseInt(available, 10)) {
                alert("반품 가능 수량(" + available + ")을 초과할 수 없습니다.");
                e.preventDefault(); // submit 막기
            }
        });
}
</script>



</head>
<body>

<h3>入庫現況</h3>

<form action="returnProcess.do" method="post">
<table>
    <tr>
        <th>選択</th>
        <th>入庫ID</th>
        <th>発注ID</th>
        <th>商品名(商品ID)</th>
        <th>入庫数量</th>
        <th>返品可能数量</th>
        <th>入庫日</th>
    </tr>
<%
List<ReceiveInfo> receiveInfoList = (List<ReceiveInfo>) request.getAttribute("receiveInfoList");// receiveInfoList 키에 저장된 값을 가져와서 List<ReceiveInfo>형으로 변환해서 receiveInfoList 객체에 저장
																								//receiveInfoListキーに保存された値を取得し、List<ReceiveInfo>型に変換してreceiveInfoListオブジェクトに保存
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 날짜 형태의 포맷으로 변환하기 위한 객체 선언 (日付形式のフォーマットに変換するためのオブジェクト宣言)

if (receiveInfoList != null && !receiveInfoList.isEmpty()) { //receiveInfoList가 있을 경우 (입고 리스트가 있을 경우) (receiveInfoListがある場合(入庫リストがある場合))
    for (ReceiveInfo info : receiveInfoList) { // List 수만큼 for문 반복 (List数までにfor文繰り返し)
        int available = info.getAvailable_to_return();	//List에 넘어온 데이터의 getAvailable_to_return()값을 available에 대입
        												//Listに渡ってきたデータのgetAvailable_to_return()値をabailableに代入
%>
<tr>
    <td>
        <% if (available > 0) { %>
            <input type="radio" name="receiveId" value="<%= info.getReceive_id() %>"> <!-- 각 id마다 라디오버튼을 세팅 (各idごとにラジオボタンをセット) -->
        <% } else { %>
            <input type="radio" disabled> 	<!-- id가 없을 경우 세팅하지 않는데 이는 id가 있는곳에만 라디오 버튼이 생긴다는것 -->
            								<!-- idがない場合はセットしないが、これはidがあるところにだけラジオボタンができるということ -->
        <% } %>
    </td>
    <td><%= info.getReceive_id() %></td>
    <td><%= info.getOrder_id() %></td>
    <td><%= info.getProduct_name() %>(<%= info.getProduct_id() %>)</td>
    <td><%= info.getQuantity() %></td>
    <td>
    	<%= available %>
    	<input type="hidden" name="available_<%= info.getReceive_id() %>" value="<%= available %>">
    </td>
    <td><%= info.getReceive_date() != null ? sdf.format(info.getReceive_date()) : "" %></td> <!-- 날짜 형태로 나타내기 위한 변환 위의 sdf 참고 (日付の形で表すための変換上のsdf参考) -->
</tr>
<%
    }
} else {
%>
<tr>
    <td colspan="7" style="text-align:center;">返品可能な入荷品はございません。</td>
</tr>
<%
}
%>
</table>

<p>
    返品数量 : <input type="text" name="quantity"/>
    返品事由 : <input type="text" name="reason"/>
</p>
<p>
    <input type="submit" value="返品申請">
    <input type="button" value="戻る" onclick="location.href='returnInfoList.do'">
</p>
</form>

</body>
</html>