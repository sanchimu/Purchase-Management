package com.purchase.service.ReceiveInfo;

import com.purchase.dao.ReceiveInfo.ReceiveInfoDao;
import com.purchase.vo.ReceiveInfo;
import jdbc.connection.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReceiveInfoService {
    private ReceiveInfoDao dao = new ReceiveInfoDao();

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

    public String getProductIdByReceiveId(String receiveId) throws Exception {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.getProductIdByReceiveId(conn, receiveId);
        }
    }
    
    public List<ReceiveInfo> getReceiveInfoWithReturnQty() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectReceiveInfoWithReturnQty(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
}
