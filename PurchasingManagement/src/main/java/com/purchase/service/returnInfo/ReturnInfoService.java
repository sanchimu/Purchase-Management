package com.purchase.service.returnInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.purchase.dao.ReceiveInfo.ReceiveInfoDao;
import com.purchase.dao.returnInfo.ReturnInfoDao;
import com.purchase.vo.ReturnInfo;

import jdbc.connection.ConnectionProvider;

public class ReturnInfoService {
	private ReturnInfoDao returnInfoDao = new ReturnInfoDao();
	private ReceiveInfoDao receiveInfoDao = new ReceiveInfoDao();
	
	public void addReturnInfo(ReturnInfo returnInfo) { // 반품 정보 생성용 메서드
		try(Connection conn = ConnectionProvider.getConnection()){ //DB에 연결할 Connection 객체를 생성
			returnInfoDao.insert(conn, returnInfo);	//상품 생성에 필요한 productDao의 메서드
		}catch(SQLException e) {
			throw new RuntimeException(e);		//에러 발생 시 예외 처리
		}
	}
	
	//이하 부분도 위와 같이 Connection을 위해 생성된 메서드들이 전체여서 각 메서드들의 역할만 간단히 기재
	
	public void deleteReturnInfo(String[] returnInfoIds) { // 반품 정보 삭제용 메서드
		try(Connection conn = ConnectionProvider.getConnection()){
			for (String id : returnInfoIds) { // 해당 for문 설명 productService 등 product 관련에서 많이 설명 returnInfoIds에 있는 리스트 순서대로 id에 대입 반복
	            returnInfoDao.delete(conn, id);
	        }
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<ReturnInfo> getAllReturnInfo() { // 모든 반품정보를 들고오는 메서드
	    try (Connection conn = ConnectionProvider.getConnection()) {
	        return returnInfoDao.selectAll(conn);
	    } catch (SQLException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	public List<ReturnInfo> getReturnInfosByConditions(Map<String, String> conditions) { // 반품 정보 상태를 나타내는 메서든인데 사용하는곳이 없으나 일단 유지 (반품 정보쪽은 협의를 통해 상태 정보를 뺀 상태)
		try (Connection conn = ConnectionProvider.getConnection()) {
			return returnInfoDao.selectByConditions(conn, conditions);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getProductIdByReceiveId(String receiveId) { // 현재 시스템상 재고관리가 없기때문에 입고된 품목에서 반품을 진행 중인데 이때 입고 id에 따라서 입고된 상품이 뭔지도 알기위해 입고 id를 통해서 상품 id를 들고오기 위한 메서
		try(Connection conn = ConnectionProvider.getConnection()) {
			return receiveInfoDao.getProductIdByReceiveId(conn, receiveId);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
