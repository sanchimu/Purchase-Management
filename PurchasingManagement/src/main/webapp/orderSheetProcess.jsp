<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.purchase.vo.OrderSheet" %>
<%@ page import="com.purchase.controller.OrderSheetController" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>

<%
    request.setCharacterEncoding("UTF-8");
    String action = request.getParameter("action");
    OrderSheetController controller = new OrderSheetController();
    
    if ("insert".equals(action)) {
        // 발주서 등록 처리
        try {
            String orderId = request.getParameter("order_id");
            String requestId = request.getParameter("request_id");
            String supplierId = request.getParameter("supplier_id");
            String orderDateStr = request.getParameter("order_date");
            String orderStatus = request.getParameter("order_status");
            
            String result = controller.processOrderSheetInsert(orderId, requestId, supplierId, orderDateStr, orderStatus);
            
            if (result.startsWith("SUCCESS")) {
%>
                <script>
                    alert('<%= result.substring(8) %>');
                    location.href = 'orderSheet.jsp';
                </script>
<%
            } else {
%>
                <script>
                    alert('<%= result.substring(6) %>');
                    history.back();
                </script>
<%
            }
        } catch (Exception e) {
%>
            <script>
                alert('발주서 등록 중 오류가 발생했습니다: <%= e.getMessage() %>');
                history.back();
            </script>
<%
        }
        
    } else if ("updateStatus".equals(action)) {
        // 발주서 상태 변경 처리
        try {
            String orderId = request.getParameter("order_id");
            String orderStatus = request.getParameter("order_status");
            
            String result = controller.processOrderSheetStatusUpdate(orderId, orderStatus);
            
            if (result.startsWith("SUCCESS")) {
%>
                <script>
                    alert('<%= result.substring(8) %>');
                    location.href = 'orderSheet.jsp';
                </script>
<%
            } else {
%>
                <script>
                    alert('<%= result.substring(6) %>');
                    history.back();
                </script>
<%
            }
        } catch (Exception e) {
%>
            <script>
                alert('발주서 상태 변경 중 오류가 발생했습니다: <%= e.getMessage() %>');
                history.back();
            </script>
<%
        }
        
    } else if ("delete".equals(action)) {
        // 발주서 삭제 처리
        try {
            String orderId = request.getParameter("order_id");
            
            String result = controller.processOrderSheetDelete(orderId);
            
            if (result.startsWith("SUCCESS")) {
%>
                <script>
                    alert('<%= result.substring(8) %>');
                    location.href = 'orderSheet.jsp';
                </script>
<%
            } else {
%>
                <script>
                    alert('<%= result.substring(6) %>');
                    history.back();
                </script>
<%
            }
        } catch (Exception e) {
%>
            <script>
                alert('발주서 삭제 중 오류가 발생했습니다: <%= e.getMessage() %>');
                history.back();
            </script>
<%
        }
        
    } else {
        // 잘못된 액션
%>
        <script>
            alert('잘못된 요청입니다.');
            location.href = 'orderSheet.jsp';
        </script>
<%
    }
%>