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

    /** 조인 기반 목록 (onlyAvailable: 반품가능>0만, includeHidden: 숨김(A/X) 포함 여부) */
    public List<ReceiveInfo> getReceiveListJoin(boolean onlyAvailable, boolean includeHidden) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectAllView(conn, onlyAvailable, includeHidden);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
}
