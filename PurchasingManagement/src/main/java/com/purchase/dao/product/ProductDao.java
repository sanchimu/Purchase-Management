package com.purchase.dao.product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.purchase.vo.Product;

import jdbc.JdbcUtil;

public class ProductDao {

    public Product insert(Connection conn, Product product) throws SQLException {
        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            int seqNum = 0;
            pstmt = conn.prepareStatement("select product_seq.nextval from dual");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                seqNum = rs.getInt(1);
            }
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);

            // product_id: "P" + 3자리 숫자 (예: P001)
            String productId = String.format("P%03d", seqNum);
            product.setProduct_id(productId);

            // ✅ 컬럼 명시: row_status는 DB 기본값('A') 적용
            String sql = "INSERT INTO product "
                       + "(product_id, product_name, category, price, supplier_id, product_status) "
                       + "VALUES (?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, product.getProduct_id());
            pstmt.setString(2, product.getProduct_name());
            pstmt.setString(3, product.getCategory());
            pstmt.setInt(4, product.getPrice());
            pstmt.setString(5, product.getSupplier_id());
            pstmt.setString(6, product.getProduct_status());
            int insertedCount = pstmt.executeUpdate();

            if (insertedCount > 0) {
                // 방금 생성한 값 반환 (row_status는 기본 'A')
                // 굳이 다시 SELECT 하지 않아도 되지만, 기존 패턴 유지
                JdbcUtil.close(pstmt);
                stmt = conn.createStatement();
                rs = stmt.executeQuery(
                    "SELECT product_id, product_name, category, price, supplier_id, product_status, row_status "
                  + "FROM product "
                  + "WHERE product_id = '" + product.getProduct_id() + "'"
                );
                if (rs.next()) {
                    return new Product(
                        rs.getString("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getInt("price"),
                        rs.getString("supplier_id"),
                        rs.getString("product_status"),
                        rs.getString("row_status")
                    );
                }
            }
            return null;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
            JdbcUtil.close(stmt);
        }
    }

    public int delete(Connection conn, String pno) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "DELETE FROM product WHERE product_id = ?")) {
            pstmt.setString(1, pno);
            return pstmt.executeUpdate();
        }
    }

    public List<Product> selectAll(Connection conn) throws SQLException {
        String sql =
            "SELECT product_id, product_name, category, price, supplier_id, product_status, row_status " +
            "  FROM product " +
            " ORDER BY product_id";
        List<Product> list = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Product p = new Product(
                    rs.getString("product_id"),
                    rs.getString("product_name"),
                    rs.getString("category"),
                    rs.getInt("price"),
                    rs.getString("supplier_id"),
                    rs.getString("product_status"),
                    rs.getString("row_status")
                );
                list.add(p);
            }
        }
        return list;
    }

    public List<Product> selectByConditions(Connection conn, Map<String, String> conditions) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT product_id, product_name, category, price, supplier_id, product_status, row_status " +
            "  FROM product WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (conditions.get("product_id") != null && !conditions.get("product_id").isEmpty()) {
            sql.append(" AND product_id = ?");
            params.add(conditions.get("product_id"));
        }
        if (conditions.get("product_name") != null && !conditions.get("product_name").isEmpty()) {
            sql.append(" AND product_name LIKE ?");
            params.add("%" + conditions.get("product_name") + "%");
        }
        if (conditions.get("category") != null && !conditions.get("category").isEmpty()) {
            sql.append(" AND category = ?");
            params.add(conditions.get("category"));
        }
        if (conditions.get("supplier_id") != null && !conditions.get("supplier_id").isEmpty()) {
            sql.append(" AND supplier_id = ?");
            params.add(conditions.get("supplier_id"));
        }
        sql.append(" ORDER BY product_id");

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Product> list = new ArrayList<>();
                while (rs.next()) {
                    Product p = new Product(
                        rs.getString("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getInt("price"),
                        rs.getString("supplier_id"),
                        rs.getString("product_status"),
                        rs.getString("row_status")
                    );
                    list.add(p);
                }
                return list;
            }
        }
    }

    public List<String> getCategoryList(Connection conn) {
        List<String> categoryList = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT DISTINCT category FROM product ORDER BY category");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                categoryList.add(rs.getString("category"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categoryList;
    }

    public int updateProductStatus(Connection conn, Product product) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE product SET product_status = ? WHERE product_id = ?")) {
            pstmt.setString(1, product.getProduct_status());
            pstmt.setString(2, product.getProduct_id());
            return pstmt.executeUpdate();
        }
    }
}
