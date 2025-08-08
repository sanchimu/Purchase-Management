<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.purchase.vo.ReceiveInfo" %>
<%@ page import="java.text.SimpleDateFormat" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>입고 리스트</title>
</head>
<body>
    <h2>입고 품목</h2>

    <%
        // 컨트롤러에서 전달받은 입고 목록
        List<ReceiveInfo> receiveInfoList = (List<ReceiveInfo>) request.getAttribute("receiveInfoList");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    %>

    <form action="deleteReceiveInfos.do" method="post" onsubmit="return validateForm();">
        <table border="1">
            <tr>
                <th>선택</th>
                <th>입고 ID</th>
                <th>주문 ID</th>
                <th>상품 ID</th>
                <th>수량</th>
                <th>입고일자</th>
            </tr>

            <%
                if (receiveInfoList != null && !receiveInfoList.isEmpty()) {
                    for (ReceiveInfo info : receiveInfoList) {
            %>
            <tr>
                <td><input type="checkbox" name="receiveIds" value="<%= info.getReceiveId() %>"></td>
                <td><%= info.getReceiveId() %></td>
                <td><%= info.getOrderId() %></td>
                <td><%= info.getProductId() %></td>
                <td><%= info.getQuantity() %></td>
                <td><%= info.getReceiveDate() != null ? sdf.format(info.getReceiveDate()) : "" %></td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr>
                <td colspan="6">조회된 입고 정보가 없습니다.</td>
            </tr>
            <% } %>
        </table>

        <br>
        <input type="submit" value="선택 항목 삭제">
    </form>

    <br>
    <a href="receiveInfo.do">입고 등록하기</a>

    <!-- 삭제 전 체크 여부 검사 -->
    <script>
        function validateForm() {
            const checked = document.querySelectorAll('input[name="receiveIds"]:checked');
            if (checked.length === 0) {
                alert("삭제할 항목을 하나 이상 선택하세요.");
                return false;
            }
            return true;
        }
    </script>

    <!-- 성공 메시지 알림창 -->
    <%
        String successMsg = (String) session.getAttribute("successMsg");
        if (successMsg != null) {
    %>
    <script>
        alert('<%= successMsg %>');
    </script>
    <%
            session.removeAttribute("successMsg");
        }
    %>
</body>
</html>
