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

    public void addReceiveInfo(ReceiveInfo receiveInfo) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            dao.insert(conn, receiveInfo);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ReceiveInfo> getAllReceiveInfos() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectAll(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getProductIdByReceiveId(String receiveId) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.getProductIdByReceiveId(conn, receiveId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ReceiveInfo> getReceiveInfoWithReturnQty() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectReceiveInfoWithReturnQty(conn);
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
            for (int i = 0; i < ids.length; i++) {
                dao.updateStatus(conn, ids[i], statuses.get(i));
            }
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
