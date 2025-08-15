package com.purchase.dao.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.purchase.vo.OrderSheet;

public class OrderSheetDao {

    /** ✅ 시퀀스 기반 새 ID 생성 (OR%03d) */
    public String nextOrderId(Connection conn) throws SQLException {
        String sql = "SELECT order_sheet_seq.NEXTVAL FROM dual";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int seq = rs.getInt(1);
                return String.format("OR%03d", seq);
            }
            throw new SQLException("order_sheet_seq NEXTVAL 조회 실패");
        }
    }

    /* =====================
     *  발주서 등록
     *  - order_id가 null이면 시퀀스로 보정
     *  - row_status 는 DB 기본값('A') 사용
     * ===================== */
    public void insert(Connection conn, OrderSheet order) throws SQLException {
        if (order.getOrder_id() == null || order.getOrder_id().trim().isEmpty()) {
            order.setOrder_id(nextOrderId(conn));
        }

        String sql =
            "INSERT INTO order_sheet (order_id, request_id, supplier_id, order_date, order_status) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, order.getOrder_id());
            pstmt.setString(2, order.getRequest_id());
            pstmt.setString(3, order.getSupplier_id());
            java.sql.Date od = (order.getOrder_date() == null)
                    ? new java.sql.Date(System.currentTimeMillis())
                    : new java.sql.Date(order.getOrder_date().getTime());
            pstmt.setDate(4, od);
            pstmt.setString(5, order.getOrder_status());

            pstmt.executeUpdate();
        }
    }

    /* =====================
     *  발주서 전체 조회
     * ===================== */
    public List<OrderSheet> selectAll(Connection conn) throws SQLException {
        String sql =
            "SELECT order_id, request_id, supplier_id, order_date, order_status, row_status " +
            "  FROM order_sheet " +
            " ORDER BY order_date DESC, order_id";
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
                order.setRow_status(rs.getString("row_status"));
                list.add(order);
            }
        }
        return list;
    }

    /* =====================
     *  발주서 단건 조회
     * ===================== */
    public OrderSheet selectById(Connection conn, String orderId) throws SQLException {
        String sql =
            "SELECT order_id, request_id, supplier_id, order_date, order_status, row_status " +
            "  FROM order_sheet WHERE order_id = ?";

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
                    order.setRow_status(rs.getString("row_status"));
                    return order;
                }
            }
        }
        return null;
    }

    /* =====================
     *  조건 검색
     * ===================== */
    public List<OrderSheet> selectByConditions(Connection conn, Map<String, String> cond) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT order_id, request_id, supplier_id, order_date, order_status, row_status " +
            "  FROM order_sheet WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (cond != null) {
            String v;
            v = cond.get("order_id");
            if (v != null && !v.isEmpty()) { sql.append(" AND order_id = ?"); params.add(v); }
            v = cond.get("request_id");
            if (v != null && !v.isEmpty()) { sql.append(" AND request_id = ?"); params.add(v); }
            v = cond.get("supplier_id");
            if (v != null && !v.isEmpty()) { sql.append(" AND supplier_id = ?"); params.add(v); }
        }
        sql.append(" ORDER BY order_date DESC, order_id");

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) pstmt.setObject(i + 1, params.get(i));
            List<OrderSheet> list = new ArrayList<>();
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderSheet order = new OrderSheet();
                    order.setOrder_id(rs.getString("order_id"));
                    order.setRequest_id(rs.getString("request_id"));
                    order.setSupplier_id(rs.getString("supplier_id"));
                    order.setOrder_date(rs.getDate("order_date"));
                    order.setOrder_status(rs.getString("order_status"));
                    order.setRow_status(rs.getString("row_status"));
                    list.add(order);
                }
            }
            return list;
        }
    }

    /* =====================
     *  업무상태 변경 (단건)
     * ===================== */
    public int updateOrderStatus(Connection conn, OrderSheet order) throws SQLException {
        String sql = "UPDATE order_sheet SET order_status = ? WHERE order_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, order.getOrder_status());
            pstmt.setString(2, order.getOrder_id());
            return pstmt.executeUpdate();
        }
    }

    /* =====================
     *  업무상태 변경 (일괄)
     * ===================== */
    public int updateOrderStatusBulk(Connection conn, Map<String, String> idToStatus) throws SQLException {
        if (idToStatus == null || idToStatus.isEmpty()) return 0;

        String sql = "UPDATE order_sheet SET order_status = ? WHERE order_id = ?";
        int updated = 0;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Map.Entry<String, String> e : idToStatus.entrySet()) {
                pstmt.setString(1, e.getValue());
                pstmt.setString(2, e.getKey());
                updated += pstmt.executeUpdate();
            }
        }
        return updated;
    }
}
