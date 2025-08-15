package com.purchase.dao.returnInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.purchase.vo.ReturnInfo;

public class ReturnInfoDao {

    /** 다음 반품ID 생성: RE + 3자리 (예: RE006) */
    private String nextReturnId(Connection conn) throws SQLException {
        final String sql = "SELECT return_info_seq.NEXTVAL FROM dual";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long seq = rs.getLong(1);
                return String.format("RE%03d", seq);
            }
        }
        throw new SQLException("return_info_seq.NEXTVAL 조회 실패");
    }

    /** INSERT (return_date null이면 SYSDATE) */
    public ReturnInfo insert(Connection conn, ReturnInfo r) throws SQLException {
        String newId = nextReturnId(conn);
        r.setReturn_id(newId);

        final String sql =
            "INSERT INTO return_info " +
            " (return_id, receive_id, product_id, quantity, reason, return_date) " +
            "VALUES (?, ?, ?, ?, ?, NVL(?, SYSDATE))";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getReturn_id());
            ps.setString(2, r.getReceive_id());
            ps.setString(3, r.getProduct_id());
            ps.setInt(4, r.getQuantity());
            ps.setString(5, r.getReason());
            if (r.getReturn_date() != null) {
                ps.setDate(6, new java.sql.Date(r.getReturn_date().getTime()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            ps.executeUpdate();
            return r;
        }
    }

    /** 삭제 */
    public int delete(Connection conn, String rno) throws SQLException {
        try (PreparedStatement ps =
                 conn.prepareStatement("DELETE FROM return_info WHERE return_id = ?")) {
            ps.setString(1, rno);
            return ps.executeUpdate();
        }
    }

    /** 전체 조회 (오름차순) */
    public List<ReturnInfo> selectAll(Connection conn) throws SQLException {
        final String sql =
            "SELECT return_id, receive_id, product_id, quantity, reason, return_date " +
            "FROM return_info ORDER BY return_id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<ReturnInfo> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new ReturnInfo(
                    rs.getString("return_id"),
                    rs.getString("receive_id"),
                    rs.getString("product_id"),
                    rs.getInt("quantity"),
                    rs.getString("reason"),
                    rs.getDate("return_date")
                ));
            }
            return list;
        }
    }

    /** 조건 조회 */
    public List<ReturnInfo> selectByConditions(Connection conn, Map<String,String> cond)
            throws SQLException {
        StringBuilder sb = new StringBuilder(
            "SELECT return_id, receive_id, product_id, quantity, reason, return_date " +
            "FROM return_info WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (cond.get("return_id") != null) {
            sb.append(" AND return_id = ?");
            params.add(cond.get("return_id"));
        }
        if (cond.get("receive_id") != null) {
            sb.append(" AND receive_id LIKE ?");
            params.add("%" + cond.get("receive_id") + "%");
        }
        if (cond.get("product_id") != null) {
            sb.append(" AND product_id = ?");
            params.add(cond.get("product_id"));
        }
        sb.append(" ORDER BY return_id");

        try (PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                List<ReturnInfo> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new ReturnInfo(
                        rs.getString("return_id"),
                        rs.getString("receive_id"),
                        rs.getString("product_id"),
                        rs.getInt("quantity"),
                        rs.getString("reason"),
                        rs.getDate("return_date")
                    ));
                }
                return list;
            }
        }
    }

    /** 단건 조회 (업데이트 검증용) */
    public ReturnInfo selectById(Connection conn, String returnId) throws SQLException {
        final String sql =
            "SELECT return_id, receive_id, product_id, quantity, reason, return_date " +
            "FROM return_info WHERE return_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, returnId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ReturnInfo(
                        rs.getString("return_id"),
                        rs.getString("receive_id"),
                        rs.getString("product_id"),
                        rs.getInt("quantity"),
                        rs.getString("reason"),
                        rs.getDate("return_date")
                    );
                }
                return null;
            }
        }
    }

    /** 받았던 수량 (receive_info.quantity) */
    public int getReceivedQty(Connection conn, String receiveId) throws SQLException {
        final String sql = "SELECT quantity FROM receive_info WHERE receive_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, receiveId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return 0;
            }
        }
    }

    /** 해당 입고건의 (자기 자신 제외) 기존 반품 합계 */
    public int getReturnedQtyExcluding(Connection conn, String receiveId, String excludeReturnId)
            throws SQLException {
        String sql = "SELECT NVL(SUM(quantity),0) FROM return_info WHERE receive_id = ?";
        boolean hasExclude = (excludeReturnId != null && !excludeReturnId.isEmpty());
        if (hasExclude) sql += " AND return_id <> ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, receiveId);
            if (hasExclude) ps.setString(2, excludeReturnId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return 0;
            }
        }
    }

    /** 수량/사유 업데이트 */
    public int updateQuantityAndReason(Connection conn, String returnId, int qty, String reason)
            throws SQLException {
        final String sql =
            "UPDATE return_info SET quantity = ?, reason = ? WHERE return_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setString(2, reason);
            ps.setString(3, returnId);
            return ps.executeUpdate();
        }
    }
}
