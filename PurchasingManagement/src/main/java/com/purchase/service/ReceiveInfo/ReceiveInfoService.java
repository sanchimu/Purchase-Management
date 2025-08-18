package com.purchase.service.ReceiveInfo;

import com.purchase.dao.ReceiveInfo.ReceiveInfoDao;
import com.purchase.vo.ReceiveInfo;
import jdbc.connection.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ReceiveInfoService {
    private final ReceiveInfoDao dao = new ReceiveInfoDao();

    /** 입고 등록 */
    public void addReceiveInfo(ReceiveInfo receiveInfo) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            dao.insert(conn, receiveInfo);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** (이전 호환) 단순 전체 목록 */
    public List<ReceiveInfo> getAllReceiveInfos() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectAll(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 조인 기반 목록
     * @param onlyAvailable   true면 (반품가능수량 > 0)만
     * @param includeHidden   true면 row_status='X' 등 숨김도 포함
     */
    public List<ReceiveInfo> getReceiveListJoin(boolean onlyAvailable, boolean includeHidden) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            // ➜ ReceiveInfoDao에 selectAllView(conn, onlyAvailable, includeHidden) 가 있어야 합니다.
            return dao.selectAllView(conn, onlyAvailable, includeHidden);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** (호환용) 반품가능수량만 필요한 화면에서 사용 — 기존 핸들러가 이 메서드명을 호출했을 때 대비 */
    public List<ReceiveInfo> getReceiveInfoWithReturnQty() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectReceiveInfoWithReturnQty(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    /** (호환용) 상품명/공급업체명 + 반품가능수량이 필요한 화면에서 사용 — 기존 핸들러 대비 */
    public List<ReceiveInfo> getReceiveInfoWithNamesAndReturnQty() {
        // 동일하게 조인 결과 사용 (이 메서드명을 기대하는 JSP/핸들러가 있어서 유지)
        return getReceiveListJoin(true, false);
    }

    /** 드롭다운에 쓸 업무상태 세트 */
    public List<String> getReceiveStatusList() {
        return Arrays.asList("검수중", "정상", "입고 취소", "반품 처리");
    }

    /** 선택 항목 상태 저장 */
    public void updateStatuses(String[] ids, List<String> statuses) {
        if (ids == null || ids.length == 0) return;
        try (Connection conn = ConnectionProvider.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (int i = 0; i < ids.length; i++) {
                    dao.updateStatus(conn, ids[i], statuses.get(i));
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public String getProductIdByReceiveId(String receiveId) throws Exception {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.getProductIdByReceiveId(conn, receiveId);
        }
    }
}

