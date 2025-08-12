package com.purchase.dao.returnInfo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.purchase.vo.ReturnInfo;

import jdbc.JdbcUtil;

public class ReturnInfoDao {
	public ReturnInfo insert(Connection conn, ReturnInfo returnInfo)throws SQLException {
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			int seqNum = 0;
			pstmt = conn.prepareStatement("select return_info_seq.nextval from dual");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				seqNum = rs.getInt(1);
			}
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);

			// product_id 만들기: "P" + 3자리 숫자 (ex. P001)
			String returnId = String.format("RI%02d", seqNum);
			returnInfo.setReturn_id(returnId);
			
			pstmt = conn.prepareStatement("INSERT INTO RETURN_INFO VALUES (?,?,?,?,?,?)");
			pstmt.setString(1, returnInfo.getReturn_id());
			pstmt.setString(2, returnInfo.getReceive_id());
			pstmt.setString(3, returnInfo.getProduct_id());
			pstmt.setInt(4, returnInfo.getQuantity());
			pstmt.setString(5, returnInfo.getReason());
			pstmt.setDate(6,  new Date( returnInfo.getReturn_date() .getTime()) );
			int insertedCount = pstmt.executeUpdate();
			
			  if (insertedCount > 0) {
	                stmt = conn.createStatement();
	                rs = stmt.executeQuery(
	                        "select * from (select return_id from return_info order by return_id desc) where rownum = 1");
	                if (rs.next()) {
	                    return new ReturnInfo(
	                        returnInfo.getReturn_id(),
	                        returnInfo.getReceive_id(),
	                        returnInfo.getProduct_id(),
	                        returnInfo.getQuantity(),
	                        returnInfo.getReason(),
	                        returnInfo.getReturn_date()
	                    );
	                }
			  }
			return null;
		}finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
            JdbcUtil.close(stmt);
		}
	}
	
	public int delete (Connection conn, String rno) throws SQLException{
		try(PreparedStatement pstmt = conn.prepareStatement("delete from return_info where return_id = ?")){
			pstmt.setString(1, rno);
			return pstmt.executeUpdate();
		}
	}
	
	
	public List<ReturnInfo> selectAll(Connection conn) throws SQLException {
		String sql = "SELECT * FROM RETURN_INFO ORDER BY RETURN_ID";
		List<ReturnInfo> returnInfoList = new ArrayList<>();
	    System.out.println("DAO selectAll 실행");
		int i = 0;
		try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				ReturnInfo r = new ReturnInfo(rs.getString("return_id"), rs.getString("receive_id"),
						rs.getString("product_id"), rs.getInt("quantity"), rs.getString("reason"),
						rs.getDate("return_date"));
				returnInfoList.add(r);
				i++;
			}
			System.out.println("조회된 데이터 수: " + i);
		}
		return returnInfoList;
	}

}
