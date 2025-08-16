package com.purchase.service.order;

import com.purchase.dao.order.OrderSheetDao;
import com.purchase.dto.OrderSheetDTO;
import com.purchase.vo.OrderSheet;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class OrderSheetService {

    private final OrderSheetDao dao = new OrderSheetDao();

    /** 목록(조인) */
    public List<OrderSheetDTO> getAllView(boolean includeHidden) throws SQLException {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectAllView(conn, includeHidden);
        }
    }

    /** 드롭다운용 요청 옵션 */
    public List<OrderSheetDTO> getRequestOptions() throws SQLException {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectRequestOptions(conn);
        }
    }

    /** 요청ID → 공급업체ID (서버 검증/자동결정) */
    public String findSupplierIdByRequestId(String requestId) throws SQLException {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.findSupplierIdByRequestId(conn, requestId);
        }
    }

    /** 다음 발주ID */
    public String generateNextOrderId() throws SQLException {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.nextOrderId(conn);
        }
    }

    /** 발주 등록 */
    public void insert(OrderSheet order) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);
            dao.insert(conn, order);
            conn.commit();
        } catch (SQLException e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    /** 업무상태 저장(단건) */
    public void updateOrderStatus(OrderSheet os) throws SQLException {
        try (Connection conn = ConnectionProvider.getConnection()) {
            dao.updateStatus(conn, os);
        }
    }

    /** 표시상태(A/X) 일괄 변경 */
    public void setRowStatusBulk(String[] ids, String rowStatus) throws SQLException {
        if (ids == null || ids.length == 0) return;
        try (Connection conn = ConnectionProvider.getConnection()) {
            conn.setAutoCommit(false);
            for (String id : ids) {
                if (id != null && !id.isBlank()) {
                    dao.updateRowStatus(conn, id, rowStatus);
                }
            }
            conn.commit();
        }
    }

    /** 업무상태 허용 세트 */
    public List<String> getOrderStatusList() {
        return Arrays.asList("요청","승인","발주","부분입고","완료","취소","보류");
    }
}
