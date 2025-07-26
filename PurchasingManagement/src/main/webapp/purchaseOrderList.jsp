<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.purchase.service.PurchaseOrderService"%>
<%@ page import="com.purchase.vo.OrderSheet"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>발주서 목록</title>
<style>
body {
	font-family: Arial, sans-serif;
	margin: 20px;
	background-color: #f5f5f5;
}

.container {
	max-width: 1000px;
	margin: 0 auto;
	background: white;
	padding: 30px;
	border-radius: 8px;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header {
	text-align: center;
	border-bottom: 2px solid #333;
	padding-bottom: 20px;
	margin-bottom: 30px;
}

.table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 20px;
}

.table th, .table td {
	border: 1px solid #ddd;
	padding: 12px;
	text-align: left;
}

.table th {
	background-color: #f8f9fa;
	font-weight: bold;
}

.table tbody tr:hover {
	background-color: #f5f5f5;
}

.btn {
	padding: 10px 20px;
	background-color: #007bff;
	color: white;
	text-decoration: none;
	border-radius: 4px;
	margin: 5px;
	display: inline-block;
}

.btn:hover {
	background-color: #0056b3;
}

.status-pending {
	color: orange;
	font-weight: bold;
}

.status-completed {
	color: green;
	font-weight: bold;
}

.status-cancelled {
	color: red;
	font-weight: bold;
}
</style>
</head>
<body>
	<div class="container">
		<div class="header">
			<h1>발주서 목록</h1>
			<p>Purchase Order List</p>
		</div>

		<div style="margin-bottom: 20px;">
			<a href="purchaseOrderForm.jsp" class="btn">새 발주서 작성</a>
		</div>

		<table class="table">
			<thead>
				<tr>
					<th>발주번호</th>
					<th>요청번호</th>
					<th>공급업체ID</th>
					<th>발주날짜</th>
					<th>상태</th>
					<th>작업</th>
				</tr>
			</thead>
			<tbody>
				<%
				try {
					PurchaseOrderService service = new PurchaseOrderService();
					List<OrderSheet> orders = service.getRecentOrders(20);

					if (orders != null && !orders.isEmpty()) {
						for (OrderSheet order : orders) {
					String statusClass = "";
					if ("요청".equals(order.getOrderStatus())) {
						statusClass = "status-pending";
					} else if ("완료".equals(order.getOrderStatus())) {
						statusClass = "status-completed";
					} else if ("취소".equals(order.getOrderStatus())) {
						statusClass = "status-cancelled";
					}
				%>
				<tr>
					<td><%=order.getOrderId()%></td>
					<td><%=order.getRequestId()%></td>
					<td><%=order.getSupplierId()%></td>
					<td><%=order.getOrderDate()%></td>
					<td><span class="<%=statusClass%>"><%=order.getOrderStatus()%></span></td>
					<td><a
						href="purchaseOrderDetail.jsp?orderId=<%=order.getOrderId()%>"
						class="btn" style="font-size: 12px; padding: 5px 10px;">상세보기</a></td>
				</tr>
				<%
				}
				} else {
				%>
				<tr>
					<td colspan="6" style="text-align: center;">등록된 발주서가 없습니다.</td>
				</tr>
				<%
				}
				} catch (Exception e) {
				%>
				<tr>
					<td colspan="6" style="text-align: center; color: red;">데이터를
						불러올 수 없습니다: <%=e.getMessage()%>
					</td>
				</tr>
				<%
				}
				%>
			</tbody>
		</table>
	</div>
</body>
</html>