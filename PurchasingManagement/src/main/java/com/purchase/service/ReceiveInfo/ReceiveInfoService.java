package com.purchase.service.ReceiveInfo;

import com.purchase.dao.ReceiveInfo.ReceiveInfoDao;
import com.purchase.vo.ReceiveInfo;
import jdbc.connection.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReceiveInfoService {
    private ReceiveInfoDao dao = new ReceiveInfoDao();

    public void addReceiveInfo(ReceiveInfo vo) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            dao.insert(conn, vo);
        } catch (SQLException e) {
            throw new RuntimeException("DB 저장 실패", e);
        }
    }

    public List<ReceiveInfo> getAllReceiveInfos() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectAll(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
