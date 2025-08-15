package com.purchase.service.returnInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.purchase.dao.ReceiveInfo.ReceiveInfoDao;
import com.purchase.dao.returnInfo.ReturnInfoDao;
import com.purchase.vo.ReturnInfo;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class ReturnInfoService {

    private final ReturnInfoDao returnInfoDao = new ReturnInfoDao();
    private final ReceiveInfoDao receiveInfoDao = new ReceiveInfoDao();

    /** 반품 신규 등록 (입고 가용수량 검증 포함) */
    public void addReturnInfo(ReturnInfo returnInfo) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            // 가용수량 검증
            final String receiveId = returnInfo.getReceive_id();
            final int receivedQty  = returnInfoDao.getReceivedQty(conn, receiveId);                 // 입고 총수량
            final int returnedSum  = returnInfoDao.getReturnedQtyExcluding(conn, receiveId, null); // 기존 반품합(신규이므로 제외ID 없음)
            final int maxAllowed   = receivedQty - returnedSum;

            if (returnInfo.getQuantity() <= 0) {
                throw new IllegalArgumentException("반품 수량은 1 이상이어야 합니다.");
            }
            if (returnInfo.getQuantity() > maxAllowed) {
                throw new IllegalArgumentException("반품 가능 수량은 최대 " + maxAllowed + "개입니다.");
            }

            // 실제 INSERT (DAO가 return_id 생성)
            returnInfoDao.insert(conn, returnInfo);

            conn.commit();
        } catch (SQLException e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException(e);
        } catch (RuntimeException re) { // 검증 실패 등
            JdbcUtil.rollback(conn);
            throw re;
        } finally {
            JdbcUtil.close(conn);
        }
    }

    /** 반품 다건 삭제 (선택 기능, 사용 안 하면 핸들러/매핑 삭제해도 무방) */
    public void deleteReturnInfo(String[] returnInfoIds) {
        if (returnInfoIds == null || returnInfoIds.length == 0) return;

        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            for (String id : returnInfoIds) {
                if (id != null && !id.isBlank()) {
                    returnInfoDao.delete(conn, id);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    /** 전체 목록 조회 */
    public List<ReturnInfo> getAllReturnInfo() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return returnInfoDao.selectAll(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 조건 검색 */
    public List<ReturnInfo> getReturnInfosByConditions(Map<String, String> conditions) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return returnInfoDao.selectByConditions(conn, conditions);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** receive_id로 product_id 조회 (반품 등록시 사용) */
    public String getProductIdByReceiveId(String receiveId) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return receiveInfoDao.getProductIdByReceiveId(conn, receiveId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 단건 업데이트(수량/사유) + 가용수량 재검증 */
    public void updateReturnInfo(String returnId, int newQty, String newReason) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            // 현재 레코드 조회
            ReturnInfo current = returnInfoDao.selectById(conn, returnId);
            if (current == null) {
                throw new IllegalArgumentException("존재하지 않는 반품 ID입니다: " + returnId);
            }

            // 가용수량 재계산(자기 자신 제외)
            final String receiveId = current.getReceive_id();
            final int receivedQty  = returnInfoDao.getReceivedQty(conn, receiveId);
            final int returnedSumOthers = returnInfoDao.getReturnedQtyExcluding(conn, receiveId, returnId);
            final int maxAllowed = receivedQty - returnedSumOthers;

            if (newQty <= 0) {
                throw new IllegalArgumentException("반품 수량은 1 이상이어야 합니다.");
            }
            if (newQty > maxAllowed) {
                throw new IllegalArgumentException("반품 가능 수량은 최대 " + maxAllowed + "개입니다.");
            }

            // 업데이트 실행
            returnInfoDao.updateQuantityAndReason(conn, returnId, newQty, newReason);

            conn.commit();
        } catch (SQLException e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException(e);
        } catch (RuntimeException re) {
            JdbcUtil.rollback(conn);
            throw re;
        } finally {
            JdbcUtil.close(conn);
        }
    }
}
