package com.purchase.dao.request;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.purchase.vo.PurchaseRequest;

import jdbc.JdbcUtil;

public class PurchaseRequestDao {

	// 주문 요청 생성 (request_status, row_status는 DB 기본값 사용)
	public PurchaseRequest insert(Connection conn, PurchaseRequest req) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			int seqNum = 0;
			pstmt = conn.prepareStatement("SELECT purchase_request_seq.NEXTVAL FROM dual");
			rs = pstmt.executeQuery();
			if (rs.next())
				seqNum = rs.getInt(1);
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);

			String requestId = String.format("PR%03d", seqNum);
			req.setRequest_id(requestId);

			pstmt = conn.prepareStatement("INSERT INTO purchase_request "
					+ "  (request_id, product_id, quantity, request_date, requester_name) " + "VALUES (?,?,?,?,?)");
			pstmt.setString(1, req.getRequest_id());
			pstmt.setString(2, req.getProduct_id());
			pstmt.setInt(3, req.getQuantity());
			java.sql.Date sqlDate = (req.getRequest_date() == null) ? new java.sql.Date(System.currentTimeMillis())
					: new java.sql.Date(req.getRequest_date().getTime());
			pstmt.setDate(4, sqlDate);
			pstmt.setString(5, req.getRequester_name());

			int inserted = pstmt.executeUpdate();
			if (inserted > 0)
				return req;
			return null;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// (정책상 UI에서 미사용) 단건 삭제 – 남겨두어도 되고, 제거해도 무방
	public int delete(Connection conn, String requestId) throws SQLException {
		try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM purchase_request WHERE request_id = ?")) {
			pstmt.setString(1, requestId);
			return pstmt.executeUpdate();
		}
	}

	// (정책상 UI에서 미사용) 여러 요청 삭제
	public int deleteMany(Connection conn, List<String> ids) throws SQLException {
		if (ids == null || ids.isEmpty())
			return 0;

		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM purchase_request WHERE request_id IN (");
		for (int i = 0; i < ids.size(); i++) {
			if (i > 0)
				sb.append(",");
			sb.append("?");
		}
		sb.append(")");

		try (PreparedStatement pstmt = conn.prepareStatement(sb.toString())) {
			for (int i = 0; i < ids.size(); i++) {
				pstmt.setString(i + 1, ids.get(i));
			}
			return pstmt.executeUpdate();
		}
	}

	// 전체 주문 요청 조회 (request_status, row_status 포함)
	public List<PurchaseRequest> selectAll(Connection conn) throws SQLException {
		String sql = "SELECT request_id, product_id, quantity, request_date, requester_name, "
				+ "       request_status, row_status " + "  FROM purchase_request " + " ORDER BY request_id";

		List<PurchaseRequest> list = new ArrayList<>();
		try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				PurchaseRequest pr = new PurchaseRequest();
				pr.setRequest_id(rs.getString("request_id"));
				pr.setProduct_id(rs.getString("product_id"));
				pr.setSupplier_id(rs.getString("supplier_id"));
				java.math.BigDecimal q = rs.getBigDecimal("quantity");
				pr.setQuantity(q == null ? null : q.intValue());

				pr.setRequest_date(rs.getDate("request_date"));
				pr.setRequester_name(rs.getString("requester_name"));
				pr.setRequest_status(rs.getString("request_status")); // ✅ 업무상태
				pr.setRow_status(rs.getString("row_status")); // ✅ 표시상태(A/X)
				list.add(pr);
			}
		}
		return list;
	}

	// ✅ 조건 검색 (request_id, product_id, requester_name)
	public List<PurchaseRequest> selectByConditions(Connection conn, Map<String, String> cond) throws SQLException {
		StringBuilder sql = new StringBuilder(
				"SELECT r.request_id, r.product_id, " + "       p.supplier_id AS supplier_id, " + // ★ product에서 공급업체ID
						"       r.quantity, r.request_date, r.requester_name, "
						+ "       r.request_status, r.row_status " + "  FROM purchase_request r "
						+ "  JOIN product p ON p.product_id = r.product_id " + " WHERE 1=1");

		List<Object> params = new ArrayList<>();

		if (cond != null) {
			String v;
			v = cond.get("request_id");
			if (v != null && !v.isEmpty()) {
				sql.append(" AND r.request_id = ?"); // ★ r. 접두사
				params.add(v);
			}
			v = cond.get("product_id");
			if (v != null && !v.isEmpty()) {
				sql.append(" AND r.product_id = ?"); // ★ r. 접두사
				params.add(v);
			}
			v = cond.get("requester_name");
			if (v != null && !v.isEmpty()) {
				sql.append(" AND r.requester_name LIKE ?");
				params.add("%" + v + "%");
			}
		}

		// 정렬은 취향대로; 기존대로 가려면 " ORDER BY r.request_id"
		sql.append(" ORDER BY r.request_date DESC, r.request_id DESC");

		try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(i + 1, params.get(i));
			}
			try (ResultSet rs = pstmt.executeQuery()) {
				List<PurchaseRequest> list = new ArrayList<>();
				while (rs.next()) {
					PurchaseRequest pr = new PurchaseRequest();
					pr.setRequest_id(rs.getString("request_id"));
					pr.setProduct_id(rs.getString("product_id"));
					pr.setSupplier_id(rs.getString("supplier_id")); // ★ alias 동일

					// ★ NUMBER → 안전 변환 (BigDecimal 캐스팅 예외 방지)
					Number q = (Number) rs.getObject("quantity");
					pr.setQuantity(q == null ? null : q.intValue());

					pr.setRequest_date(rs.getDate("request_date"));
					pr.setRequester_name(rs.getString("requester_name"));
					pr.setRequest_status(rs.getString("request_status"));
					pr.setRow_status(rs.getString("row_status"));
					list.add(pr);
				}
				return list;
			}
		}
	}

	// 상품 ID로 검색 (request_status, row_status 포함)
	public List<PurchaseRequest> selectByProductId(Connection conn, String productId) throws SQLException {
		final String sql = "SELECT r.request_id, r.product_id, " + "       p.supplier_id AS supplier_id, " + // ★
																												// product에서
																												// 공급업체ID
				"       r.quantity, r.request_date, r.requester_name, " + "       r.request_status, r.row_status "
				+ "  FROM purchase_request r " + "  JOIN product p ON p.product_id = r.product_id "
				+ " WHERE r.product_id = ? " + " ORDER BY r.request_date DESC, r.request_id DESC";

		List<PurchaseRequest> list = new ArrayList<>();
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, productId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					PurchaseRequest pr = new PurchaseRequest();
					pr.setRequest_id(rs.getString("request_id"));
					pr.setProduct_id(rs.getString("product_id"));
					pr.setSupplier_id(rs.getString("supplier_id")); // ★ alias와 동일

					// ★ NUMBER → 안전 변환 (BigDecimal 캐스팅 예외 방지)
					Number q = (Number) rs.getObject("quantity");
					pr.setQuantity(q == null ? null : q.intValue());

					pr.setRequest_date(rs.getDate("request_date"));
					pr.setRequester_name(rs.getString("requester_name"));
					pr.setRequest_status(rs.getString("request_status"));
					pr.setRow_status(rs.getString("row_status"));

					list.add(pr);
				}
			}
		}
		return list;
	}

	public List<PurchaseRequest> selectRequestListJoined(Connection conn) throws SQLException {
		final String sql = "SELECT r.request_id, r.product_id, " + "       p.supplier_id AS supplier_id, " + // ← alias로
																												// 라벨 고정
				"       r.quantity, r.request_date, r.requester_name, " + "       r.request_status, r.row_status "
				+ "  FROM purchase_request r " + "  JOIN product p ON p.product_id = r.product_id "
				+ " ORDER BY r.request_date DESC, r.request_id DESC";

		try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

			List<PurchaseRequest> list = new ArrayList<>();
			while (rs.next()) {
				PurchaseRequest pr = new PurchaseRequest();
				pr.setRequest_id(rs.getString("request_id"));
				pr.setProduct_id(rs.getString("product_id"));
				pr.setSupplier_id(rs.getString("supplier_id")); // alias 동일

				// ✅ quantity 안전 변환 (ClassCastException 방지)
				Number q = (Number) rs.getObject("quantity");
				pr.setQuantity(q == null ? null : q.intValue());

				pr.setRequest_date(rs.getDate("request_date"));
				pr.setRequester_name(rs.getString("requester_name"));
				pr.setRequest_status(rs.getString("request_status"));
				pr.setRow_status(rs.getString("row_status"));

				list.add(pr);
			}
			return list;
		}
	}

	// ✅ 업무상태 변경 (드롭다운 저장 시 사용)
	public int updateRequestStatus(Connection conn, PurchaseRequest pr) throws SQLException {
		String sql = "UPDATE purchase_request SET request_status = ? WHERE request_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, pr.getRequest_status());
			pstmt.setString(2, pr.getRequest_id());
			return pstmt.executeUpdate();
		}
	}
}
