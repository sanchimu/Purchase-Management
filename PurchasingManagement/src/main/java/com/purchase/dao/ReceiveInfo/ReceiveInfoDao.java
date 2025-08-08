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
	        pstmt.setString(1, vo.getOrderId());
	        pstmt.setString(2, vo.getProductId());
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
                vo.setReceiveId(rs.getString("receive_id"));
                vo.setOrderId(rs.getString("order_id"));
                vo.setProductId(rs.getString("product_id"));
                vo.setQuantity(rs.getInt("quantity"));
                vo.setReceiveDate(rs.getDate("receive_date"));
                list.add(vo);
            }
        }
        return list;
    }
}

