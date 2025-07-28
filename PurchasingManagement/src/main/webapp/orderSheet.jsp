<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.purchase.vo.OrderSheet" %>
<%@ page import="com.purchase.controller.OrderSheetController" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>발주서 관리</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .form-container { background-color: #f9f9f9; padding: 20px; margin-bottom: 20px; }
        .btn { padding: 5px 10px; margin: 2px; background-color: #4CAF50; color: white; border: none; cursor: pointer; }
        .btn:hover { background-color: #45a049; }
        .btn-delete { background-color: #f44336; }
        .btn-delete:hover { background-color: #da190b; }
        .status-pending { color: orange; }
        .status-approved { color: green; }
        .status-completed { color: blue; }
    </style>
</head>
<body>
    <h1>발주서 관리</h1>
    
    <!-- 발주서 등록 폼 -->
    <div class="form-container">
        <h2>발주서 등록</h2>
        <form action="orderSheetProcess.jsp" method="post">
            <input type="hidden" name="action" value="insert">
            <table>
                <tr>
                    <td>발주서 ID:</td>
                    <td><input type="text" name="order_id" required></td>
                </tr>
                <tr>
                    <td>구매요청 ID:</td>
                    <td><input type="text" name="request_id" required></td>
                </tr>
                <tr>
                    <td>공급업체 ID:</td>
                    <td><input type="text" name="supplier_id" required></td>
                </tr>
                <tr>
                    <td>발주일:</td>
                    <td><input type="date" name="order_date" required></td>
                </tr>
                <tr>
                    <td>상태:</td>
                    <td>
                        <select name="order_status" required>
                            <option value="대기">대기</option>
                            <option value="승인">승인</option>
                            <option value="완료">완료</option>
                            <option value="취소">취소</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <input type="submit" value="등록하기" class="btn">
                        <input type="reset" value="초기화" class="btn">
                    </td>
                </tr>
            </table>
        </form>
    </div>
    
    <!-- 발주서 목록 조회 -->
    <h2>발주서 목록</h2>
    
    <%
        OrderSheetController controller = new OrderSheetController();
        List<OrderSheet> orderList = controller.getOrderSheetList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    %>
    
    <table>
        <thead>
            <tr>
                <th>발주서 ID</th>
                <th>구매요청 ID</th>
                <th>공급업체 ID</th>
                <th>발주일</th>
                <th>상태</th>
                <th>관리</th>
            </tr>
        </thead>
        <tbody>
            <% if (orderList != null && !orderList.isEmpty()) { %>
                <% for (OrderSheet order : orderList) { %>
                    <tr>
                        <td><%= order.getOrder_id() %></td>
                        <td><%= order.getRequest_id() %></td>
                        <td><%= order.getSupplier_id() %></td>
                        <td><%= sdf.format(order.getOrder_date()) %></td>
                        <td>
                            <span class="status-<%= order.getOrder_status().toLowerCase() %>">
                                <%= order.getOrder_status() %>
                            </span>
                        </td>
                        <td>
                            <!-- 상태 변경 버튼들 -->
                            <% if (!"완료".equals(order.getOrder_status()) && !"취소".equals(order.getOrder_status())) { %>
                                <form style="display:inline;" action="orderSheetProcess.jsp" method="post">
                                    <input type="hidden" name="action" value="updateStatus">
                                    <input type="hidden" name="order_id" value="<%= order.getOrder_id() %>">
                                    <select name="order_status" onchange="this.form.submit()">
                                        <option value="">상태변경</option>
                                        <option value="대기" <%= "대기".equals(order.getOrder_status()) ? "selected" : "" %>>대기</option>
                                        <option value="승인" <%= "승인".equals(order.getOrder_status()) ? "selected" : "" %>>승인</option>
                                        <option value="완료" <%= "완료".equals(order.getOrder_status()) ? "selected" : "" %>>완료</option>
                                        <option value="취소" <%= "취소".equals(order.getOrder_status()) ? "selected" : "" %>>취소</option>
                                    </select>
                                </form>
                            <% } %>
                            
                            <!-- 삭제 버튼 -->
                            <form style="display:inline;" action="orderSheetProcess.jsp" method="post" 
                                  onsubmit="return confirm('정말 삭제하시겠습니까?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="order_id" value="<%= order.getOrder_id() %>">
                                <input type="submit" value="삭제" class="btn btn-delete">
                            </form>
                        </td>
                    </tr>
                <% } %>
            <% } else { %>
                <tr>
                    <td colspan="6">등록된 발주서가 없습니다.</td>
                </tr>
            <% } %>
        </tbody>
    </table>
    
    <!-- 검색 기능 -->
    <div class="form-container">
        <h3>발주서 검색</h3>
        <form action="orderSheet.jsp" method="get">
            <table>
                <tr>
                    <td>구매요청 ID로 검색:</td>
                    <td>
                        <input type="text" name="search_request_id" value="<%= request.getParameter("search_request_id") != null ? request.getParameter("search_request_id") : "" %>">
                        <input type="submit" value="검색" class="btn">
                    </td>
                </tr>
            </table>
        </form>
        
        <% 
            String searchRequestId = request.getParameter("search_request_id");
            if (searchRequestId != null && !searchRequestId.trim().isEmpty()) {
                List<OrderSheet> searchResults = controller.searchOrderSheetsByRequestId(searchRequestId);
        %>
        
        <h4>검색 결과 (구매요청 ID: <%= searchRequestId %>)</h4>
        <table>
            <thead>
                <tr>
                    <th>발주서 ID</th>
                    <th>구매요청 ID</th>
                    <th>공급업체 ID</th>
                    <th>발주일</th>
                    <th>상태</th>
                </tr>
            </thead>
            <tbody>
                <% if (searchResults != null && !searchResults.isEmpty()) { %>
                    <% for (OrderSheet order : searchResults) { %>
                        <tr>
                            <td><%= order.getOrder_id() %></td>
                            <td><%= order.getRequest_id() %></td>
                            <td><%= order.getSupplier_id() %></td>
                            <td><%= sdf.format(order.getOrder_date()) %></td>
                            <td><%= order.getOrder_status() %></td>
                        </tr>
                    <% } %>
                <% } else { %>
                    <tr>
                        <td colspan="5">검색된 발주서가 없습니다.</td>
                    </tr>
                <% } %>
            </tbody>
        </table>
        
        <% } %>
    </div>
    
    <script>
        // 폼 유효성 검사
        function validateForm(form) {
            var inputs = form.querySelectorAll('input[required]');
            for (var i = 0; i < inputs.length; i++) {
                if (!inputs[i].value.trim()) {
                    alert('모든 필수 항목을 입력해주세요.');
                    inputs[i].focus();
                    return false;
                }
            }
            return true;
        }
        
        // 등록 폼에 유효성 검사 추가
        document.querySelector('form[action="orderSheetProcess.jsp"]').onsubmit = function() {
            return validateForm(this);
        };
    </script>
</body>
</html>