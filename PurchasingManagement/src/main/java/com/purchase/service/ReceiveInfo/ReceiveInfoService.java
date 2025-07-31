package com.purchase.service.ReceiveInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.purchase.dao.ReceiveInfo.ReceiveInfoDao;
import com.purchase.vo.ReceiveInfo;

import jdbc.connection.ConnectionProvider;

public class ReceiveInfoService {
    private ReceiveInfoDao receiveInfoDao = new ReceiveInfoDao();

    public ReceiveInfo addReceiveInfo(ReceiveInfo recInfo) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return receiveInfoDao.insert(conn, recInfo);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteReceiveInfos(String[] receiveIds) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            for (String id : receiveIds) {
                receiveInfoDao.delete(conn, id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ReceiveInfo> getAllReceiveInfos() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return receiveInfoDao.selectAll(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ReceiveInfo> getReceiveInfosByConditions(Map<String, String> conditions) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return receiveInfoDao.selectByConditions(conn, conditions);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}