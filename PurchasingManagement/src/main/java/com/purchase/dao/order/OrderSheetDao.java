package com.purchase.dao.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.purchase.vo.OrderSheet;

public class OrderSheetDao {
	
	
	// ✅ 발주서 등록
    public void insert(Connection conn, OrderSheet order) throws SQLException {
        String sql = "INSERT INTO order_sheet (order_id, request_id, supplier_id, order_date, order_status) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, order.getOrder_id());
            pstmt.setString(2, order.getRequest_id());
            pstmt.setString(3, order.getSupplier_id());
            pstmt.setDate(4, new java.sql.Date(order.getOrder_date().getTime()));
            pstmt.setString(5, order.getOrder_status());

            pstmt.executeUpdate();
        }
    }

    // ✅ 발주서 전체 조회
    public List<OrderSheet> selectAll(Connection conn) throws SQLException {
        String sql = "SELECT * FROM order_sheet ORDER BY order_date DESC";
        List<OrderSheet> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                OrderSheet order = new OrderSheet();
                order.setOrder_id(rs.getString("order_id"));
                order.setRequest_id(rs.getString("request_id"));
                order.setSupplier_id(rs.getString("supplier_id"));
                order.setOrder_date(rs.getDate("order_date"));
                order.setOrder_status(rs.getString("order_status"));

                list.add(order);
            }
        }

        return list;
    }

    // ✅ 발주서 단건 조회 (선택 사항)
    public OrderSheet selectById(Connection conn, String orderId) throws SQLException {
        String sql = "SELECT * FROM order_sheet WHERE order_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    OrderSheet order = new OrderSheet();
                    order.setOrder_id(rs.getString("order_id"));
                    order.setRequest_id(rs.getString("request_id"));
                    order.setSupplier_id(rs.getString("supplier_id"));
                    order.setOrder_date(rs.getDate("order_date"));
                    order.setOrder_status(rs.getString("order_status"));
                    return order;
                }
            }
        }

        return null;
    }
}