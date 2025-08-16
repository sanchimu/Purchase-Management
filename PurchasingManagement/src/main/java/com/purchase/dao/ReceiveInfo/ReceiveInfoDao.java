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

    /** INSERT (receive_status 입력값이 있으면 사용, 없으면 DB DEFAULT '정상') */
    public void insert(Connection conn, ReceiveInfo receiveInfo) throws SQLException {
        String newId = nextReceiveId(conn);
        receiveInfo.setReceive_id(newId);

        final String sql =
            "INSERT INTO receive_info " +
            "  (receive_id, order_id, product_id, quantity, receive_date, receive_status) " +
            "VALUES " +
            "  (?, ?, ?, ?, NVL(?, SYSDATE), NVL(?, '정상'))";

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

            ps.setString(6, receiveInfo.getReceive_status()); // null이면 DEFAULT '정상'
            ps.executeUpdate();
        }
    }

    /** 단순 전체 조회 (조인 없음) */
    public List<ReceiveInfo> selectAll(Connection conn) throws SQLException {
        final String sql =
            "SELECT receive_id, order_id, product_id, quantity, receive_date, receive_status " +
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
                ri.setReceive_status(rs.getString("receive_status"));
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

    /**
     * JOIN 한방 조회:
     *  - 상품명/공급업체명 포함
     *  - 반품가능수량(= ri.quantity - NVL(SUM(rti.quantity),0)) 계산
     *  - onlyAvailable=true 이면 HAVING > 0
     *  - includeHidden=false 이면 p/si/os 의 row_status='A'만
     */
    public List<ReceiveInfo> selectAllView(Connection conn,
                                           boolean onlyAvailable,
                                           boolean includeHidden) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append(
            "SELECT " +
            "  ri.receive_id, ri.order_id, ri.product_id, " +
            "  p.product_name, si.supplier_name, " +
            "  ri.quantity, ri.receive_date, ri.receive_status, " +
            "  (ri.quantity - NVL(SUM(rti.quantity), 0)) AS available_to_return " +
            "FROM receive_info ri " +
            "JOIN product       p  ON p.product_id  = ri.product_id " +
            "JOIN order_sheet   os ON os.order_id   = ri.order_id " +
            "JOIN supplier_info si ON si.supplier_id = os.supplier_id " +
            "LEFT JOIN return_info rti ON rti.receive_id = ri.receive_id "
        );
        // 숨김 미포함이면 진행중(A)만
        sb.append("WHERE (? = 1 OR (NVL(p.row_status,'A')='A' AND NVL(si.row_status,'A')='A' AND NVL(os.row_status,'A')='A')) ");
        sb.append(
            "GROUP BY " +
            "  ri.receive_id, ri.order_id, ri.product_id, " +
            "  p.product_name, si.supplier_name, " +
            "  ri.quantity, ri.receive_date, ri.receive_status "
        );
        if (onlyAvailable) {
            sb.append("HAVING (ri.quantity - NVL(SUM(rti.quantity), 0)) > 0 ");
        }
        sb.append("ORDER BY ri.receive_id");

        String sql = sb.toString();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, includeHidden ? 1 : 0); // WHERE (?=1 OR …)
            try (ResultSet rs = ps.executeQuery()) {
                List<ReceiveInfo> list = new ArrayList<>();
                while (rs.next()) {
                    ReceiveInfo vo = new ReceiveInfo();
                    vo.setReceive_id(rs.getString("receive_id"));
                    vo.setOrder_id(rs.getString("order_id"));
                    vo.setProduct_id(rs.getString("product_id"));
                    vo.setProduct_name(rs.getString("product_name"));
                    vo.setSupplier_name(rs.getString("supplier_name"));
                    vo.setQuantity(rs.getInt("quantity"));
                    vo.setReceive_date(rs.getDate("receive_date"));
                    vo.setReceive_status(rs.getString("receive_status"));
                    vo.setAvailable_to_return(rs.getInt("available_to_return"));
                    list.add(vo);
                }
                return list;
            }
        }
    }

    /** 상태 변경 */
    public int updateStatus(Connection conn, String receiveId, String status) throws SQLException {
        final String sql = "UPDATE receive_info SET receive_status = ? WHERE receive_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, receiveId);
            return ps.executeUpdate();
        }
    }
}
