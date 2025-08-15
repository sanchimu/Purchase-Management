package com.purchase.dao.ReceiveInfo;

import com.purchase.vo.ReceiveInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceiveInfoDao {

    /** RI + 3자리 시퀀스 (예: RI001, RI002…) */
    private String nextReceiveId(Connection conn) throws SQLException {
        final String sql = "SELECT receive_info_seq.NEXTVAL FROM dual";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int seq = rs.getInt(1);
                return String.format("RI%03d", seq);
            }
        }
        throw new SQLException("receive_info_seq.NEXTVAL 조회 실패");
    }

    /** INSERT (receive_date 가 null이면 SYSDATE 사용) */
    public void insert(Connection conn, ReceiveInfo receiveInfo) throws SQLException {
        String newId = nextReceiveId(conn);
        receiveInfo.setReceive_id(newId);

        final String sql =
            "INSERT INTO receive_info (receive_id, order_id, product_id, quantity, receive_date) " +
            "VALUES (?, ?, ?, ?, NVL(?, SYSDATE))";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, receiveInfo.getReceive_id());
            ps.setString(2, receiveInfo.getOrder_id());
            ps.setString(3, receiveInfo.getProduct_id());
            ps.setInt(4, receiveInfo.getQuantity());

            if (receiveInfo.getReceive_date() != null) {
                ps.setDate(5, new java.sql.Date(receiveInfo.getReceive_date().getTime()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.executeUpdate();
        }
    }

    /** 전체 조회 (오름차순: RI001→RI002…) */
    public List<ReceiveInfo> selectAll(Connection conn) throws SQLException {
        final String sql =
            "SELECT receive_id, order_id, product_id, quantity, receive_date " +
            "FROM receive_info ORDER BY receive_id";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<ReceiveInfo> list = new ArrayList<>();
            while (rs.next()) {
                ReceiveInfo ri = new ReceiveInfo();
                ri.setReceive_id(rs.getString("receive_id"));
                ri.setOrder_id(rs.getString("order_id"));
                ri.setProduct_id(rs.getString("product_id"));
                ri.setQuantity(rs.getInt("quantity"));
                ri.setReceive_date(rs.getDate("receive_date"));
                list.add(ri);
            }
            return list;
        }
    }

    /** receive_id → product_id */
    public String getProductIdByReceiveId(Connection conn, String receiveId) throws SQLException {
        final String sql = "SELECT product_id FROM receive_info WHERE receive_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, receiveId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }
        return null;
    }

    /** 반품 가능 수량 포함 목록 (등록 화면에서 사용) */
    public List<ReceiveInfo> selectReceiveInfoWithReturnQty(Connection conn) throws SQLException {
        final String sql =
            "SELECT ri.receive_id, ri.order_id, ri.product_id, ri.quantity AS received_quantity, ri.receive_date, " +
            "       NVL(SUM(rti.quantity), 0) AS returned_quantity, " +
            "       (ri.quantity - NVL(SUM(rti.quantity), 0)) AS available_to_return " +
            "FROM receive_info ri " +
            "LEFT JOIN return_info rti ON ri.receive_id = rti.receive_id " +
            "GROUP BY ri.receive_id, ri.order_id, ri.product_id, ri.quantity, ri.receive_date " +
            "HAVING (ri.quantity - NVL(SUM(rti.quantity), 0)) > 0 " +
            "ORDER BY ri.receive_id";

        List<ReceiveInfo> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ReceiveInfo vo = new ReceiveInfo();
                vo.setReceive_id(rs.getString("receive_id"));
                vo.setOrder_id(rs.getString("order_id"));
                vo.setProduct_id(rs.getString("product_id"));
                vo.setQuantity(rs.getInt("received_quantity")); // 총 입고수량
                vo.setReceive_date(rs.getDate("receive_date"));
                vo.setAvailable_to_return(rs.getInt("available_to_return")); // 반품 가능 수량
                list.add(vo);
            }
        }
        return list;
    }

    /** (선택) 상태 변경이 DAO에 필요하면 사용 — 지금은 서비스에서 직접 UPDATE 하므로 없어도 됨 */
    public int updateStatus(Connection conn, String receiveId, String status) throws SQLException {
        final String sql = "UPDATE receive_info SET receive_status = ? WHERE receive_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, receiveId);
            return ps.executeUpdate();
        }
    }
}
