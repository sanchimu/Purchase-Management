package com.purchase.service.returnInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.purchase.dao.ReceiveInfo.ReceiveInfoDao;
import com.purchase.dao.returnInfo.ReturnInfoDao;
import com.purchase.vo.Product;
import com.purchase.vo.ReturnInfo;

import jdbc.connection.ConnectionProvider;

public class ReturnInfoService {
	private ReturnInfoDao returnInfoDao = new ReturnInfoDao();
	private ReceiveInfoDao receiveInfoDao = new ReceiveInfoDao();
	
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
	
	public List<ReturnInfo> getReturnInfosByConditions(Map<String, String> conditions) {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return returnInfoDao.selectByConditions(conn, conditions);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getProductIdByReceiveId(String receiveId) {
		try(Connection conn = ConnectionProvider.getConnection()) {
			return receiveInfoDao.getProductIdByReceiveId(conn, receiveId);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
