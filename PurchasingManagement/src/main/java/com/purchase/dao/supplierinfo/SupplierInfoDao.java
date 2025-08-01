package com.purchase.dao.supplierinfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.purchase.vo.SupplierInfo;

import jdbc.JdbcUtil;

public class SupplierInfoDao {

  
    public void insert(Connection conn, SupplierInfo supplier) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(
                "INSERT INTO supplier_info (supplier_id, supplier_name, contact_number, adress) VALUES (?, ?, ?, ?)"
            );
            pstmt.setString(1, supplier.getSupplier_id());
            pstmt.setString(2, supplier.getSupplier_name());
            pstmt.setString(3, supplier.getContact_number());
            pstmt.setString(4, supplier.getAddress()); 

            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

   
    public void delete(Connection conn, String supplierId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(
                "DELETE FROM supplier_info WHERE supplier_id = ?"
            );
            pstmt.setString(1, supplierId);

            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

   
    public List<SupplierInfo> selectByName(Connection conn, String name) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<SupplierInfo> list = new ArrayList<>();

        try {
            pstmt = conn.prepareStatement(
                "SELECT * FROM supplier_info WHERE supplier_name LIKE ?"
            );
            pstmt.setString(1, "%" + name + "%");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                SupplierInfo supplier = new SupplierInfo(
                    rs.getString("supplier_id"),
                    rs.getString("supplier_name"),
                    rs.getString("contact_number"),
                    rs.getString("adress") 
                );
                list.add(supplier);
            }
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }

        return list;
    }
}
