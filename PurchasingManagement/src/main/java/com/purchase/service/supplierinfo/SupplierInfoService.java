package com.purchase.service.supplierinfo;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.purchase.dao.supplierinfo.SupplierInfoDao;
import com.purchase.vo.SupplierInfo;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class SupplierInfoService {

    private SupplierInfoDao supplierDao = new SupplierInfoDao();

    
    public void addSupplier(SupplierInfo supplier) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            supplierDao.insert(conn, supplier);

            conn.commit(); 
        } catch (SQLException e) {
            JdbcUtil.rollback(conn); // 
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

 
    public void removeSupplier(String supplierId) {
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


    public List<SupplierInfo> searchSuppliers(String name) {
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
}
