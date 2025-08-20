package com.purchase.dao.supplierinfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.purchase.vo.SupplierInfo;

import jdbc.JdbcUtil;

/**
 * SupplierInfoDao クラス
 * - DB の supplier_info テーブルを操作する DAO クラス
 * - CRUD（登録、削除、更新、検索、全件取得など）の処理を実装
 */
public class SupplierInfoDao {

    // 登録
    // supplier_status, row_status は DB 側のデフォルト値を利用
    // supplier_status DEFAULT '有効', row_status DEFAULT 'A'
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

    // 削除処理（supplier_id を指定して物理削除）
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
    
    // 仕入先の業務状態を変更
    // supplier_status を更新（例: 有効 → 取引停止）
    public int updateSupplierStatus(Connection conn, SupplierInfo supplier) throws SQLException {
        String sql = "UPDATE supplier_info SET supplier_status = ? WHERE supplier_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplier.getSupplier_status());
            ps.setString(2, supplier.getSupplier_id());
            return ps.executeUpdate();
        }
    }

    // 名前で検索
    // supplier_status, row_status を含めてマッピングして返す
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
                s.setSupplier_status(rs.getString("supplier_status")); // 業務状態
                s.setRow_status(rs.getString("row_status"));           // 表示状態(A/X)
                list.add(s);
            }
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
        return list;
    }

    // 全件取得
    // supplier_status, row_status を含めてマッピング
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
                s.setSupplier_status(rs.getString("supplier_status")); // 業務状態
                s.setRow_status(rs.getString("row_status"));           // 表示状態
                list.add(s);
            }
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
        return list;
    }
    
    // 有効な仕入先一覧を取得（商品登録用に使用）
    // row_status = 'A' のものだけ返す
    public List<SupplierInfo> selectActiveSuppliers(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<SupplierInfo> list = new ArrayList<>();

        try {
            pstmt = conn.prepareStatement(
                "SELECT supplier_id, supplier_name, contact_number, address, " +
                "       supplier_status, row_status " +
                "  FROM supplier_info " +
                " WHERE row_status = 'A' " +
                " ORDER BY supplier_id"
            );
            rs = pstmt.executeQuery();

            while (rs.next()) {
                SupplierInfo s = new SupplierInfo();
                s.setSupplier_id(rs.getString("supplier_id"));
                s.setSupplier_name(rs.getString("supplier_name"));
                s.setContact_number(rs.getString("contact_number"));
                s.setAddress(rs.getString("address"));
                s.setSupplier_status(rs.getString("supplier_status")); // 業務状態
                s.setRow_status(rs.getString("row_status"));           // 表示状態
                list.add(s);
            }
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
        return list;
    }
}
