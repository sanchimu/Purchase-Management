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

    // 등록 (업무/표시 상태는 DB 기본값 사용: supplier_status DEFAULT '활성', row_status DEFAULT 'A')
    public void insert(Connection conn, SupplierInfo supplier) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(
                "INSERT INTO supplier_info (supplier_id, supplier_name, contact_number, address) " +
                "VALUES (?, ?, ?, ?)"
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

    // 삭제
    public void delete(Connection conn, String supplierId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("DELETE FROM supplier_info WHERE supplier_id = ?");
            pstmt.setString(1, supplierId);
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }
    
 // 공급업체 업무상태 변경
    public int updateSupplierStatus(Connection conn, SupplierInfo supplier) throws SQLException {
        String sql = "UPDATE supplier_info SET supplier_status = ? WHERE supplier_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplier.getSupplier_status());
            ps.setString(2, supplier.getSupplier_id());
            return ps.executeUpdate();
        }
    }


    // 이름 검색 (supplier_status, row_status 포함해서 매핑)
    public List<SupplierInfo> selectByName(Connection conn, String name) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<SupplierInfo> list = new ArrayList<>();

        try {
            pstmt = conn.prepareStatement(
                "SELECT supplier_id, supplier_name, contact_number, address, " +
                "       supplier_status, row_status " +
                "  FROM supplier_info " +
                " WHERE supplier_name LIKE ? " +
                " ORDER BY supplier_id"
            );
            pstmt.setString(1, "%" + name + "%");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                SupplierInfo s = new SupplierInfo();
                s.setSupplier_id(rs.getString("supplier_id"));
                s.setSupplier_name(rs.getString("supplier_name"));
                s.setContact_number(rs.getString("contact_number"));
                s.setAddress(rs.getString("address"));
                s.setSupplier_status(rs.getString("supplier_status")); // ✅ 업무상태
                s.setRow_status(rs.getString("row_status"));           // ✅ 표시상태(A/X)
                list.add(s);
            }
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
        return list;
    }

    // 전체 목록 (supplier_status, row_status 포함해서 매핑)
    public List<SupplierInfo> selectAll(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<SupplierInfo> list = new ArrayList<>();

        try {
            pstmt = conn.prepareStatement(
                "SELECT supplier_id, supplier_name, contact_number, address, " +
                "       supplier_status, row_status " +
                "  FROM supplier_info " +
                " ORDER BY supplier_id"
            );
            rs = pstmt.executeQuery();

            while (rs.next()) {
                SupplierInfo s = new SupplierInfo();
                s.setSupplier_id(rs.getString("supplier_id"));
                s.setSupplier_name(rs.getString("supplier_name"));
                s.setContact_number(rs.getString("contact_number"));
                s.setAddress(rs.getString("address"));
                s.setSupplier_status(rs.getString("supplier_status")); // ✅
                s.setRow_status(rs.getString("row_status"));           // ✅
                list.add(s);
            }
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
        return list;
    }
}
