package com.purchase.service.supplierinfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.purchase.dao.supplierinfo.SupplierInfoDao;
import com.purchase.vo.SupplierInfo;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

// Service 계층. Controller와 DAO 사이에서 비즈니스 로직 + 트랜잭션 처리 담당
public class SupplierInfoService {

    private SupplierInfoDao supplierDao = new SupplierInfoDao();

    // 공급업체 추가
    public void insertSupplier(SupplierInfo supplier) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            supplierDao.insert(conn, supplier);

            conn.commit();
        } catch (SQLException e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    // 공급업체 삭제
    public void deleteSupplier(String supplierId) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            supplierDao.delete(conn, supplierId);

            conn.commit();
        } catch (SQLException e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    // 공급업체 이름으로 검색
    public List<SupplierInfo> searchSuppliersByName(String name) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            return supplierDao.selectByName(conn, name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    // 공급업체 전체 목록 조회
    public List<SupplierInfo> getSupplierList() {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            return supplierDao.selectAll(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }
}
