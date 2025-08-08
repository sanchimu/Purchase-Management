package com.purchase.service.request;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.purchase.dao.request.PurchaseRequestDao;
import com.purchase.vo.PurchaseRequest;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class PurchaseRequestService {
    private PurchaseRequestDao requestDao = new PurchaseRequestDao();

    // 구매요청 등록 (購入リクエストの登録処理)
    public void addPurchaseRequest(PurchaseRequest request) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            requestDao.insert(conn, request);
            conn.commit(); //성공 시 커밋 (成功時はコミット)
        } catch (SQLException e) {
            JdbcUtil.rollback(conn); //실패 시 롤백 (失敗時はロールバック)
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    // 구매요청 삭제 (購入リクエストの削除処理)
    public void removePurchaseRequest(String requestId) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            requestDao.delete(conn, requestId);

            conn.commit();
        } catch (SQLException e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    // 전체 구매요청 목록 조회 (購入リクエストリスト照会)
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

    // 상품 ID로 검색 (商品IDで購入リクエストを検索)
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
}