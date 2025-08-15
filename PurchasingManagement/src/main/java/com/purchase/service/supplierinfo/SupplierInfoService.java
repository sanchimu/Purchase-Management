package com.purchase.service.supplierinfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.purchase.dao.supplierinfo.SupplierInfoDao;
import com.purchase.vo.SupplierInfo;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class SupplierInfoService {

    private final SupplierInfoDao supplierDao = new SupplierInfoDao();

    /** 공급업체 등록 (DB 기본값: supplier_status='활성', row_status='A') */
    public void insertSupplier(SupplierInfo supplier) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            supplierDao.insert(conn, supplier);
            conn.commit();
        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    /** 공급업체 전체 목록 */
    public List<SupplierInfo> getSupplierList() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return supplierDao.selectAll(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 공급업체명 LIKE 검색 */
    public List<SupplierInfo> searchSuppliersByName(String name) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return supplierDao.selectByName(conn, name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 공급업체 '업무상태' 변경 */
    public void updateSupplierStatus(SupplierInfo supplier) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            supplierDao.updateSupplierStatus(conn, supplier);
            conn.commit();
        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    /** 선택 삭제 (필요 시 사용) */
    public void deleteSuppliers(String[] supplierIds) {
        if (supplierIds == null || supplierIds.length == 0) return;
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            for (String id : supplierIds) {
                supplierDao.delete(conn, id);
            }
            conn.commit();
        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    private void rollbackQuietly(Connection conn) {
        if (conn != null) try { conn.rollback(); } catch (SQLException ignore) {}
    }
}
