package com.purchase.service.supplierinfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.purchase.dao.supplierinfo.SupplierInfoDao;
import com.purchase.vo.SupplierInfo;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

/**
 * SupplierInfoService クラス
 * - DAO を利用してビジネスロジックを実装
 * - トランザクション管理（commit/rollback）を含む
 */
public class SupplierInfoService {

    private final SupplierInfoDao supplierDao = new SupplierInfoDao();

    /** 仕入先登録 (DBデフォルト: supplier_status='有効', row_status='A') */
    public void insertSupplier(SupplierInfo supplier) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            supplierDao.insert(conn, supplier); // DAO 呼び出し
            conn.commit();                      // 正常時はコミット
        } catch (SQLException e) {
            rollbackQuietly(conn);              // 例外時はロールバック
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);               // コネクションをクローズ
        }
    }

    /** 仕入先全件取得 */
    public List<SupplierInfo> getSupplierList() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return supplierDao.selectAll(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 仕入先名 LIKE 検索 */
    public List<SupplierInfo> searchSuppliersByName(String name) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return supplierDao.selectByName(conn, name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 仕入先「業務状態」変更 */
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

    /** 選択削除（複数 ID をまとめて削除） */
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

    /** ロールバック（静かに実行、例外は無視） */
    private void rollbackQuietly(Connection conn) {
        if (conn != null) try { conn.rollback(); } catch (SQLException ignore) {}
    }
    
 // SupplierInfoService.java (추가)
    public SupplierInfo getSupplierById(String supplierId) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            return supplierDao.selectById(conn, supplierId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    public void updateSupplier(SupplierInfo supplier) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);
            supplierDao.update(conn, supplier);
            conn.commit();
        } catch (SQLException e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

}
