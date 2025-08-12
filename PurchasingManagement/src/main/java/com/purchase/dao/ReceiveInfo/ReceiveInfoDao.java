package com.purchase.dao.ReceiveInfo;

import com.purchase.vo.ReceiveInfo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceiveInfoDao {

    public int insert(Connection conn, ReceiveInfo vo) throws SQLException {
        String sql = "INSERT INTO receive_info (receive_id, order_id, product_id, quantity, receive_date) "
                   + "VALUES ('RI' || LPAD(receive_info_seq.NEXTVAL, 4, '0'), ?, ?, ?, SYSDATE)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vo.getOrder_id());  
            pstmt.setString(2, vo.getProduct_id()); 
            pstmt.setInt(3, vo.getQuantity());
            return pstmt.executeUpdate();
        }
    }

    public List<ReceiveInfo> selectAll(Connection conn) throws SQLException {
        String sql = "SELECT * FROM receive_info ORDER BY receive_date DESC";
        List<ReceiveInfo> list = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ReceiveInfo vo = new ReceiveInfo();
                vo.setReceive_id(rs.getString("receive_id"));
                vo.setOrder_id(rs.getString("order_id"));   
                vo.setProduct_id(rs.getString("product_id")); 
                vo.setQuantity(rs.getInt("quantity"));
                vo.setReceive_date(rs.getDate("receive_date")); 
                list.add(vo);
            }
        }
        return list;
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

