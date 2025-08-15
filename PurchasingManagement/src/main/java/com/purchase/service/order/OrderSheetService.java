package com.purchase.service.order;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.purchase.dao.order.OrderSheetDao;
import com.purchase.vo.OrderSheet;

import jdbc.connection.ConnectionProvider;

public class OrderSheetService {

    private final OrderSheetDao dao = new OrderSheetDao();

    /** ✅ 시퀀스로 새 발주서 ID 생성 (예: OR001) */
    public String generateNextOrderId() throws SQLException {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.nextOrderId(conn);
        }
    }

    /** ✅ 발주서 등록 */
    public void insert(OrderSheet order) throws SQLException {
        try (Connection conn = ConnectionProvider.getConnection()) {
            dao.insert(conn, order); // order_id null이어도 DAO에서 보정
        }
    }

    /** ✅ 발주서 전체 조회 (row_status 포함) */
    public List<OrderSheet> getAll() throws SQLException {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectAll(conn);
        }
    }

    /** ✅ 발주서 단건 조회 */
    public OrderSheet getById(String orderId) throws SQLException {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectById(conn, orderId);
        }
    }

    /** ✅ 조건 검색 (order_id, request_id, supplier_id) */
    public List<OrderSheet> getByConditions(Map<String, String> cond) throws SQLException {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectByConditions(conn, cond);
        }
    }

    /** ✅ 업무상태 단건 저장 */
    public void updateOrderStatus(OrderSheet order) throws SQLException {
        try (Connection conn = ConnectionProvider.getConnection()) {
            dao.updateOrderStatus(conn, order);
        }
    }

    /** ✅ 업무상태 일괄 저장 */
    public void updateOrderStatusBulk(Map<String, String> idToStatus) throws SQLException {
        if (idToStatus == null || idToStatus.isEmpty()) return;

        Connection conn = null;
        boolean oldAutoCommit = true;
        try {
            conn = ConnectionProvider.getConnection();
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            dao.updateOrderStatusBulk(conn, idToStatus);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignore) {}
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(oldAutoCommit); } catch (SQLException ignore) {}
                try { conn.close(); } catch (SQLException ignore) {}
            }
        }
    }

    /** ✅ 화면 드롭다운용 상태 목록 */
    public List<String> getOrderStatusList() {
        return Arrays.asList("요청","승인","발주","부분입고","완료","취소","보류");
    }
}
