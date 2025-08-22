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

	// 공급업체 등록 메서드
    public void insert(Connection conn, SupplierInfo supplier) throws SQLException {
        PreparedStatement pstmt = null;
        // SQL문 실행할 준비 하기 위한 객체 선언
        try {
            pstmt = conn.prepareStatement(
                "INSERT INTO supplier_info (supplier_id, supplier_name, contact_number, address) " +
                "VALUES (?, ?, ?, ?)"
            );
            // DB에 보낼 SQL문 작성, PreparedStatement로 준비. ?는 나중에 채워 넣을 자리
            pstmt.setString(1, supplier.getSupplier_id());
            pstmt.setString(2, supplier.getSupplier_name());
            pstmt.setString(3, supplier.getContact_number());
            pstmt.setString(4, supplier.getAddress());
            // 각각 자리에 맞는 값 넣어줌
            pstmt.executeUpdate();
            // SQL문 실행해서 데이터를 DB에 등록
        } finally {
            JdbcUtil.close(pstmt);
        }
        // DB작업 후 pstmt 닫음
    }

    //공급업체 전달받아서 받은 ID에 해당하는 공급업체를 DB에서 삭제
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
    
    // 전달받은 공급업체의 상태값 수정
    public int updateSupplierStatus(Connection conn, SupplierInfo supplier) throws SQLException {
    	// int는 영향을 받은 행의 수를 반환. 1개 수정되면 1 반환
        String sql = "UPDATE supplier_info SET supplier_status = ? WHERE supplier_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplier.getSupplier_status());
            ps.setString(2, supplier.getSupplier_id());
            return ps.executeUpdate();
        }
    }
    
    // 공급업체 이름을 검색해서 조건에 맞는 업체를 리스트로 돌려줌
    public List<SupplierInfo> selectByName(Connection conn, String name) throws SQLException {
    	//조건에 맞는 SupplierInfo 객체들을 모아서 리스트로 반환
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<SupplierInfo> list = new ArrayList<>();

        try {
            pstmt = conn.prepareStatement(
                "SELECT supplier_id, supplier_name, contact_number, address, " +
                "       supplier_status, row_status " +
                "  FROM supplier_info " +
                " WHERE supplier_name LIKE ? " + // 이름이 일부만 포함되어 잇어도 검색
                " ORDER BY supplier_id"
            );
            pstmt.setString(1, "%" + name + "%");
            rs = pstmt.executeQuery();

            while (rs.next()) {
            	// 검색 결과를 반복문으로 하나식 거냄
                SupplierInfo s = new SupplierInfo();
                // 결과를 담을 SupplierInfo 객체 생성
                s.setSupplier_id(rs.getString("supplier_id"));
                s.setSupplier_name(rs.getString("supplier_name"));
                s.setContact_number(rs.getString("contact_number"));
                s.setAddress(rs.getString("address"));
                s.setSupplier_status(rs.getString("supplier_status"));
                s.setRow_status(rs.getString("row_status"));
                // DB 결과에서 각 컬럼 값을 꺼내서 s에 하나씩 저장
                list.add(s);
                // 저장한 객체 s를 리스트에 추가
            }
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
        return list;
    }

    // 공급업체들을 전부 가져와서 리스트로 돌려줌
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
                SupplierInfo s = new SupplierInfo(); // 반복할 대마다 새로운 s객체 만들어짐
                s.setSupplier_id(rs.getString("supplier_id"));
                s.setSupplier_name(rs.getString("supplier_name"));
                s.setContact_number(rs.getString("contact_number"));
                s.setAddress(rs.getString("address"));
                s.setSupplier_status(rs.getString("supplier_status"));
                s.setRow_status(rs.getString("row_status"));
                list.add(s);
            }
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
        return list;
    }

    //row_status가 'A'인 데이터만 가져오는 메서드
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
                s.setSupplier_status(rs.getString("supplier_status"));
                s.setRow_status(rs.getString("row_status"));
                list.add(s);
            }
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
        return list;
    }

    // 특정 아이디를 기준으로 한 업체의 상세정보 가져오는 메서드
    public SupplierInfo selectById(Connection conn, String supplierId) throws SQLException {
        String sql = "SELECT supplier_id, supplier_name, contact_number, address, supplier_status, row_status " +
                     "FROM supplier_info WHERE supplier_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplierId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SupplierInfo s = new SupplierInfo();
                    s.setSupplier_id(rs.getString("supplier_id"));
                    s.setSupplier_name(rs.getString("supplier_name"));
                    s.setContact_number(rs.getString("contact_number"));
                    s.setAddress(rs.getString("address"));
                    s.setSupplier_status(rs.getString("supplier_status"));
                    s.setRow_status(rs.getString("row_status"));
                    return s;
                }
                return null;
            }
        }
    }

    // 공급업체 한 건의 정보를 수정
    public int update(Connection conn, SupplierInfo s) throws SQLException {
        String sql =
            "UPDATE supplier_info " +
            "   SET supplier_name = ?, " +
            "       contact_number = ?, " +
            "       address = ?, " +
            "       supplier_status = ?, " +
            "       row_status = ? " +
            " WHERE supplier_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getSupplier_name());
            ps.setString(2, s.getContact_number());
            ps.setString(3, s.getAddress());
            ps.setString(4, s.getSupplier_status());
            ps.setString(5, s.getRow_status());
            ps.setString(6, s.getSupplier_id());
            return ps.executeUpdate(); // 몇 개의 행이 수정되엇는지 리턴
        }
    }
}
