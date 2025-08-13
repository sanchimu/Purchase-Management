package com.purchase.dao.ReceiveInfo;

import com.purchase.vo.ReceiveInfo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceiveInfoDao {

    /** 시퀀스 다음값 얻기 */
    private int getNextReceiveIdSeq(Connection conn) throws SQLException {
        final String sql = "SELECT receive_info_seq.NEXTVAL FROM dual";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        throw new SQLException("receive_info_seq에서 NEXTVAL을 가져오지 못했습니다.");
    }

    /** INSERT (receive_id 자동생성 + receive_date 기본값 SYSDATE) */
    public void insert(Connection conn, ReceiveInfo receiveInfo) throws SQLException {
        // 1) 새 ID 생성: RI0006 형태
        int seqVal = getNextReceiveIdSeq(conn);
        String receiveId = String.format("RI%04d", seqVal);
        receiveInfo.setReceive_id(receiveId);

        // 2) INSERT (receive_date가 null이면 NVL로 SYSDATE 대체)
        final String sql =
            "INSERT INTO receive_info (receive_id, order_id, product_id, quantity, receive_date) " +
            "VALUES (?, ?, ?, ?, NVL(?, SYSDATE))";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, receiveInfo.getReceive_id());
            pstmt.setString(2, receiveInfo.getOrder_id());
            pstmt.setString(3, receiveInfo.getProduct_id());
            pstmt.setInt(4, receiveInfo.getQuantity());

            if (receiveInfo.getReceive_date() != null) {
                pstmt.setDate(5, new java.sql.Date(receiveInfo.getReceive_date().getTime()));
            } else {
                pstmt.setNull(5, Types.DATE); // NVL이 SYSDATE로 바꿔줌
            }

            // 디버그(원인 추적용)
            System.out.printf("[INSERT receive_info] id=%s, order=%s, product=%s, qty=%d, date=%s%n",
                    receiveInfo.getReceive_id(),
                    receiveInfo.getOrder_id(),
                    receiveInfo.getProduct_id(),
                    receiveInfo.getQuantity(),
                    receiveInfo.getReceive_date());

            pstmt.executeUpdate();
        }
    }

    /** 전체 조회 */
    public List<ReceiveInfo> selectAll(Connection conn) throws SQLException {
        final String sql = "SELECT receive_id, order_id, product_id, quantity, receive_date " +
                           "FROM receive_info ORDER BY receive_date DESC, receive_id DESC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

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

    
    public String getProductIdByReceiveId(Connection conn, String receiveId) throws SQLException{
    	String productId = null;
    	
    	try(PreparedStatement pstmt = conn.prepareStatement("SELECT PRODUCT_ID FROM RECEIVE_INFO WHERE RECEIVE_ID = ?")){
    		pstmt.setString(1, receiveId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    productId = rs.getString("PRODUCT_ID");
                }
                
    	}
    	return productId;
    }
    
}
    
    public List<ReceiveInfo> selectReceiveInfoWithReturnQty(Connection conn) throws SQLException {
        String sql = "SELECT ri.receive_id, ri.order_id, ri.product_id, ri.quantity AS received_quantity, ri.receive_date, "
                   + "       NVL(SUM(rti.quantity), 0) AS returned_quantity, "
                   + "       ri.quantity - NVL(SUM(rti.quantity), 0) AS available_to_return "
                   + "FROM receive_info ri "
                   + "LEFT JOIN return_info rti ON ri.receive_id = rti.receive_id "
                   + "GROUP BY ri.receive_id, ri.order_id, ri.product_id, ri.quantity, ri.receive_date "
                   + "HAVING ri.quantity - NVL(SUM(rti.quantity), 0) > 0 "
                   + "ORDER BY ri.receive_date DESC";

        List<ReceiveInfo> list = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ReceiveInfo vo = new ReceiveInfo();
                vo.setReceive_id(rs.getString("receive_id"));
                vo.setOrder_id(rs.getString("order_id"));
                vo.setProduct_id(rs.getString("product_id"));
                vo.setQuantity(rs.getInt("received_quantity"));  // 총 입고 수량
                vo.setReceive_date(rs.getDate("receive_date"));

                // 반품 가능 수량을 따로 필드로 가지고 있지 않으면 ReceiveInfo에 새 필드 추가 필요
                vo.setAvailable_to_return(rs.getInt("available_to_return"));

                list.add(vo);
            }
        }
        return list;
    }
}

