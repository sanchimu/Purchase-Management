package com.purchase.dao.request;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.purchase.vo.PurchaseRequest;

import jdbc.JdbcUtil;

public class PurchaseRequestDao {

	// 주문 요청 생성
	public PurchaseRequest insert(Connection conn, PurchaseRequest req) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// 시퀀스를 이용하여 주문번호 생성
		try {
			int seqNum = 0; // 시퀀스 번호 저장 변수
			pstmt = conn.prepareStatement("select purchase_request_seq.nextval from dual");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				seqNum = rs.getInt(1); // 결과가 있으면 첫번째 컬럼의 값을 seqNum에 저장
			}
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);

			// 시퀀스를 기반으로 request_id 생성
			String requestId = String.format("PR%03d", seqNum);
			req.setRequest_id(requestId);

			pstmt = conn.prepareStatement(
					"INSERT INTO purchase_request (request_id, product_id, quantity, request_date, requester_name) VALUES (?, ?, ?, ?, ?)");
			pstmt.setString(1, req.getRequest_id());
			pstmt.setString(2, req.getProduct_id());
			pstmt.setInt(3, req.getQuantity());

			// java.sql.Date 형식으로 변환
			java.sql.Date sqlDate = new java.sql.Date(req.getRequest_date().getTime());
			pstmt.setDate(4, sqlDate);
			pstmt.setString(5, req.getRequester_name());
			int insertedCount = pstmt.executeUpdate();

			// 성공적으로 삽입되었을 시 해당 vo 반환
			if (insertedCount > 0) {
				return req;
			}

			return null;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 주문 요청 삭제
	public int delete(Connection conn, String requestId) throws SQLException {
		try (PreparedStatement pstmt = conn.prepareStatement("delete from purchase_request where request_id = ?")) {
			pstmt.setString(1, requestId);
			return pstmt.executeUpdate();
		}
	}
	
	//여러가지 요청 삭제
	public int deleteMany(Connection conn, List<String> ids) throws SQLException {
        if (ids == null || ids.isEmpty()) return 0;

        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM purchase_request WHERE request_id IN (");
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) sb.append(",");
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

	// 전체 주문 요청 조회
	public List<PurchaseRequest> selectAll(Connection conn) throws SQLException {
		String sql = "SELECT * FROM purchase_request ORDER BY request_id";
		List<PurchaseRequest> list = new ArrayList<>();
		try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				PurchaseRequest pr = new PurchaseRequest(rs.getString("request_id"), rs.getString("product_id"),
						rs.getInt("quantity"), rs.getDate("request_date"), rs.getString("requester_name"));
				list.add(pr);
			}
		}
		// 최종 조회된 전체 목록 반환
		return list;
	}

	//상품 ID로 검색하기
	public List<PurchaseRequest> selectByProductId(Connection conn, String productId) throws SQLException {
		String sql = "SELECT * FROM purchase_request WHERE product_id = ? ORDER BY request_date DESC";

		List<PurchaseRequest> list = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, productId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					PurchaseRequest pr = new PurchaseRequest(rs.getString("request_id"), rs.getString("product_id"),
							rs.getInt("quantity"), rs.getDate("request_date"), rs.getString("requester_name"));
					list.add(pr);
				}
			}
		}
		return list;
	}
}