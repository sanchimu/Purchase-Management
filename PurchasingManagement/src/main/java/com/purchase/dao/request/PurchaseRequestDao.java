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

	// 구매 요청 생성 (request_status, row_status는 DB 기본값 사용)
	// 購買リクエストの作成 (request_status, row_statusはDBデフォルト値を使用)
	public PurchaseRequest insert(Connection conn, PurchaseRequest req) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			int seqNum = 0;
			
			// 시퀀스를 이용하여 새로운 번호 가져오기 
			// シーケンスを利用して新しい番号をもたらす
			pstmt = conn.prepareStatement("SELECT purchase_request_seq.NEXTVAL FROM dual");
			rs = pstmt.executeQuery();
			if (rs.next())
				seqNum = rs.getInt(1);
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);

			// ID 형식: PR001 
			// ID形式: PR001
			String requestId = String.format("PR%03d", seqNum);
			req.setRequest_id(requestId);

			pstmt = conn.prepareStatement("INSERT INTO purchase_request "
					+ "  (request_id, product_id, quantity, request_date, requester_name) " + "VALUES (?,?,?,?,?)");
			pstmt.setString(1, req.getRequest_id());
			pstmt.setString(2, req.getProduct_id());
			pstmt.setInt(3, req.getQuantity());
			
			// 날짜가 없으면 현재 날짜 사용 
			// 日付がなければ現在日付を使用
			java.sql.Date sqlDate = (req.getRequest_date() == null) ? new java.sql.Date(System.currentTimeMillis())
					: new java.sql.Date(req.getRequest_date().getTime());
			pstmt.setDate(4, sqlDate);
			pstmt.setString(5, req.getRequester_name());

			// SQL 실행 후 반환 
			// SQL実行後返す
			int inserted = pstmt.executeUpdate();
			if (inserted > 0)
				return req;
			return null;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 전체 주문 요청 조회 (request_status, row_status 포함)
	// 全購買リクエスト照会 (request_status, row_statusを含む)
	public List<PurchaseRequest> selectAll(Connection conn) throws SQLException {
		String sql = "SELECT request_id, product_id, quantity, request_date, requester_name, "
				+ "       request_status, row_status " + "  FROM purchase_request " + " ORDER BY TO_NUMBER(SUBSTR(request_id,3)) ASC"
						+ "";

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
				pr.setRequest_status(rs.getString("request_status")); 
				pr.setRow_status(rs.getString("row_status"));
				list.add(pr);
			}
		}
		return list;
	}

	// 조건 검색 (request_id, product_id, requester_name)
	// 条件検索 (request_id, product_id, requester_name)
	public List<PurchaseRequest> selectByConditions(Connection conn, Map<String, String> cond) throws SQLException {
		StringBuilder sql = new StringBuilder(
				// product 테이블과 JOIN하여 supplier_id 조회 
				// product テーブルとJOINしてsupplier_idを照会
				"SELECT r.request_id, r.product_id, " + "       p.supplier_id AS supplier_id, " + // ★ product에서 공급업체ID
						"       r.quantity, r.request_date, r.requester_name, "
						+ "       r.request_status, r.row_status " + "  FROM purchase_request r "
						+ "  JOIN product p ON p.product_id = r.product_id " + " WHERE 1=1");

		List<Object> params = new ArrayList<>();

		// 검색 조건 맵이 전달되었을 때만 아래 로직 실행 
		// 検索条件のマップが渡された場合にのみ以下を実行
		if (cond != null) {
			String v;
			v = cond.get("request_id");
			
			// 일치하는 데이터 검색 후 파라미터 목록에 추가
			//一致するデータを検索した後、パラメータリストに追加
			if (v != null && !v.isEmpty()) {
				sql.append(" AND r.request_id = ?");
				params.add(v);
			}
			v = cond.get("product_id");
			if (v != null && !v.isEmpty()) {
				sql.append(" AND r.product_id = ?");
				params.add(v);
			}
			v = cond.get("requester_name");
			if (v != null && !v.isEmpty()) {
				sql.append(" AND r.requester_name LIKE ?");
				params.add("%" + v + "%");
			}
		}

		// 정렬: 최신 요청일, ID 기준 오름차순 
		// 並び替え: 最新要請日とID基準で
		sql.append(" ORDER BY r.request_date ASC, r.request_id ASC");

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

					//수량은 Number로 안전 획득
					//数量はNumberで安全に取得
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

	// product와 조인하여 요청 목록을 조회 
	// productテーブルとJOINしてリクエストリストを照会
	public List<PurchaseRequest> selectRequestListJoined(Connection conn) throws SQLException {
		final String sql = "SELECT r.request_id, r.product_id, " + "       p.supplier_id AS supplier_id, " + // ← alias로
																												// 라벨 고정
				"       r.quantity, r.request_date, r.requester_name, " + "       r.request_status, r.row_status "
				+ "  FROM purchase_request r " + "  JOIN product p ON p.product_id = r.product_id "
				+ " ORDER BY r.request_id ASC";

		try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

			List<PurchaseRequest> list = new ArrayList<>();
			while (rs.next()) {
				PurchaseRequest pr = new PurchaseRequest();
				pr.setRequest_id(rs.getString("request_id"));
				pr.setProduct_id(rs.getString("product_id"));
				pr.setSupplier_id(rs.getString("supplier_id")); // alias 동일

				// 수량은 Number로 안전 획득
				// 数量はNumberで安全に取得
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

	// 업무상태 변경 (드롭다운 저장 시 사용)
	// 業務状態変更 (ドロップダウン保存時に使用)
	public int updateRequestStatus(Connection conn, PurchaseRequest pr) throws SQLException {
		String sql = "UPDATE purchase_request SET request_status = ? WHERE request_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, pr.getRequest_status());
			pstmt.setString(2, pr.getRequest_id());
			return pstmt.executeUpdate();
		}
	}

	// 단일 요청 상세를 ID로 조회
	// 単一リクエストの詳細をIDで照会
	public com.purchase.vo.PurchaseRequest selectById(java.sql.Connection conn, String requestId)
			throws java.sql.SQLException {
		String sql = "SELECT request_id, product_id, quantity, request_date, requester_name, request_status, row_status "
				+ "FROM purchase_request WHERE request_id = ?";
		try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, requestId);
			try (java.sql.ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					com.purchase.vo.PurchaseRequest pr = new com.purchase.vo.PurchaseRequest();
					pr.setRequest_id(rs.getString("request_id"));
					pr.setProduct_id(rs.getString("product_id"));
					pr.setQuantity(rs.getInt("quantity"));
					pr.setRequest_date(rs.getDate("request_date"));
					pr.setRequester_name(rs.getString("requester_name"));
					pr.setRequest_status(rs.getString("request_status"));
					pr.setRow_status(rs.getString("row_status"));
					return pr;
				}
			}
		}
		return null;
	}

	// 수량 변경 (업데이트)  
	// 数量変更 (アップデート)
	public int updateQuantity(java.sql.Connection conn, String requestId, int quantity) throws java.sql.SQLException {
		String sql = "UPDATE purchase_request SET quantity = ? WHERE request_id = ?";
		try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, quantity);
			pstmt.setString(2, requestId);
			return pstmt.executeUpdate();
		}
	}

}
