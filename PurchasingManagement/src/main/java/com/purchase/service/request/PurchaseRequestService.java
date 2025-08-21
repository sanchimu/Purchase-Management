package com.purchase.service.request;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.purchase.dao.request.PurchaseRequestDao;
import com.purchase.vo.PurchaseRequest;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class PurchaseRequestService {

	private final PurchaseRequestDao requestDao = new PurchaseRequestDao();

	/** 구매요청 등록 */
	public void addPurchaseRequest(PurchaseRequest request) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			requestDao.insert(conn, request);

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/** 전체 구매요청 목록 */
	public List<PurchaseRequest> getPurchaseRequestList() {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return requestDao.selectAll(conn);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/** (호환용) getAll → 내부적으로 전체 조회 */
	public List<PurchaseRequest> getAll() {
		return getPurchaseRequestList();
	}

	/** 조건 검색 (request_id, product_id, requester_name) */
	public List<PurchaseRequest> getByConditions(Map<String, String> cond) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return requestDao.selectByConditions(conn, cond);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/** 상품 ID로 검색 */
	public List<PurchaseRequest> searchByProductId(String productId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return requestDao.selectByProductId(conn, productId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public List<PurchaseRequest> getJoinedRequestList() {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return requestDao.selectRequestListJoined(conn);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/** ✅ 업무상태 단건 변경 (예: '접수'→'검토중' 등) */
	public void updateRequestStatus(PurchaseRequest request) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			requestDao.updateRequestStatus(conn, request);

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/** ✅ 업무상태 일괄 변경 (요청ID→상태 맵) */
	public void updateRequestStatusBulk(Map<String, String> idToStatus) {
		if (idToStatus == null || idToStatus.isEmpty())
			return;

		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			for (Map.Entry<String, String> e : idToStatus.entrySet()) {
				PurchaseRequest pr = new PurchaseRequest();
				pr.setRequest_id(e.getKey());
				pr.setRequest_status(e.getValue());
				requestDao.updateRequestStatus(conn, pr);
			}

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
	public PurchaseRequest getById(String requestId) {
	    java.sql.Connection conn = null;
	    try {
	        conn = jdbc.connection.ConnectionProvider.getConnection();
	        return requestDao.selectById(conn, requestId);
	    } catch (java.sql.SQLException e) {
	        throw new RuntimeException(e);
	    } finally {
	        jdbc.JdbcUtil.close(conn);
	    }
	}

	public void updatePurchaseRequestQuantity(String requestId, int quantity) {
	    java.sql.Connection conn = null;
	    try {
	        conn = jdbc.connection.ConnectionProvider.getConnection();
	        conn.setAutoCommit(false);

	        requestDao.updateQuantity(conn, requestId, quantity);

	        conn.commit();
	    } catch (java.sql.SQLException e) {
	        jdbc.JdbcUtil.rollback(conn);
	        throw new RuntimeException(e);
	    } finally {
	        jdbc.JdbcUtil.close(conn);
	    }
	}

}
