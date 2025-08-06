package com.purchase.service.returnInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.purchase.dao.ReceiveInfo.ReturnInfoDao;
import com.purchase.vo.ReturnInfo;

import jdbc.connection.ConnectionProvider;

public class ReturnInfoService {
	private ReturnInfoDao returnInfoDao = new ReturnInfoDao();
	
	public void addReturnInfo(ReturnInfo returnInfo) {
		try(Connection conn = ConnectionProvider.getConnection()){
			returnInfoDao.insert(conn, returnInfo);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void deleteReturnInfo(String[] returnInfoIds) {
		try(Connection conn = ConnectionProvider.getConnection()){
			for (String id : returnInfoIds) {
	            returnInfoDao.delete(conn, id);
	        }
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<ReturnInfo> getAllReturnInfo() {
	    try (Connection conn = ConnectionProvider.getConnection()) {
	        //System.out.println(">> DB 연결 URL: " + conn.getMetaData().getURL());
	        //System.out.println(">> DB 연결 User: " + conn.getMetaData().getUserName());
	        List<ReturnInfo> list = returnInfoDao.selectAll(conn);
	        System.out.println(">> 조회된 반품 수: " + list.size());
	        return list;
	    	
			/* return returnInfoDao.selectAll(conn); */
	    } catch (SQLException e) {
	        throw new RuntimeException(e);
	    }
	}
}
