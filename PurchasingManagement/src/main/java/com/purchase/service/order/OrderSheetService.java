package com.purchase.service.order;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.purchase.dao.order.OrderSheetDao;
import com.purchase.vo.OrderSheet;

import jdbc.connection.ConnectionProvider;

public class OrderSheetService {
	
	private OrderSheetDao dao = new OrderSheetDao();

    // ✅ 발주서 등록
    public void insert(OrderSheet order) throws SQLException {
        try (Connection conn = ConnectionProvider.getConnection()) {
            dao.insert(conn, order);
        }
    }

    // ✅ 발주서 전체 조회
    public List<OrderSheet> getAll() throws SQLException {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectAll(conn);
        }
    }

    // ✅ 발주서 단건 조회 (선택)
    public OrderSheet getById(String orderId) throws SQLException {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectById(conn, orderId);
        }
    }
}
