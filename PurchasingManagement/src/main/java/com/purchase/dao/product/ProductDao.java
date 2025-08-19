package com.purchase.dao.product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // for legacy pattern in insert()
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.purchase.vo.Product;

import jdbc.JdbcUtil;

public class ProductDao {

    private static final String SELECT_BASE =
        "SELECT p.product_id, p.product_name, p.category, p.price, " +
        "       p.supplier_id, s.supplier_name, p.product_status, p.row_status " +
        "FROM product p " +
        "LEFT JOIN supplier_info s ON p.supplier_id = s.supplier_id ";

    public Product insert(Connection conn, Product product) throws SQLException {
        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            int seqNum = 0;
            pstmt = conn.prepareStatement("SELECT product_seq.nextval FROM dual");
            rs = pstmt.executeQuery();
            if (rs.next()) seqNum = rs.getInt(1);
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);

            String productId = String.format("P%03d", seqNum);
            product.setProduct_id(productId);

            String sql = "INSERT INTO product " +
                         "(product_id, product_name, category, price, supplier_id, product_status) " +
                         "VALUES (?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, product.getProduct_id());
            pstmt.setString(2, product.getProduct_name());
            pstmt.setString(3, product.getCategory());
            pstmt.setInt(4, product.getPrice());
            pstmt.setString(5, product.getSupplier_id());
            pstmt.setString(6, product.getProduct_status());
            int insertedCount = pstmt.executeUpdate();

            if (insertedCount > 0) {
                JdbcUtil.close(pstmt);
                stmt = conn.createStatement();
                rs = stmt.executeQuery(
                    "SELECT product_id, product_name, category, price, supplier_id, product_status, row_status " +
                    "FROM product WHERE product_id = '" + product.getProduct_id() + "'"
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

    public List<Product> selectAll(Connection conn) throws SQLException {
        String sql = SELECT_BASE + " ORDER BY p.product_id";
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
                    rs.getString("supplier_name"),
                    rs.getString("product_status"),
                    rs.getString("row_status")
                );
                list.add(p);
            }
        }
        return list;
    }

    /** conditions: product_id, product_name, category, supplier_id, includeHidden("1"이면 A/X 모두) */
    public List<Product> selectByConditions(Connection conn, Map<String, String> conditions) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_BASE + " WHERE 1=1");
        List<Object> params = new ArrayList<>();

        boolean includeHidden = "1".equals(conditions.get("includeHidden"));
        if (!includeHidden) {
            sql.append(" AND (p.row_status IS NULL OR p.row_status='A')");
        }

        if (conditions.get("product_id") != null && !conditions.get("product_id").isEmpty()) {
            sql.append(" AND p.product_id = ?");
            params.add(conditions.get("product_id"));
        }
        if (conditions.get("product_name") != null && !conditions.get("product_name").isEmpty()) {
            sql.append(" AND p.product_name LIKE ?");
            params.add("%" + conditions.get("product_name") + "%");
        }
        if (conditions.get("category") != null && !conditions.get("category").isEmpty()) {
            sql.append(" AND p.category = ?");
            params.add(conditions.get("category"));
        }
        if (conditions.get("supplier_id") != null && !conditions.get("supplier_id").isEmpty()) {
            sql.append(" AND p.supplier_id = ?");
            params.add(conditions.get("supplier_id"));
        }
        sql.append(" ORDER BY p.product_id");

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
                        rs.getString("supplier_name"),
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
        String sql = "SELECT DISTINCT category FROM product WHERE category IS NOT NULL ORDER BY category";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
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
        try (PreparedStatement pstmt =
                 conn.prepareStatement("UPDATE product SET product_status = ? WHERE product_id = ?")) {
            pstmt.setString(1, product.getProduct_status());
            pstmt.setString(2, product.getProduct_id());
            return pstmt.executeUpdate();
        }
    }

    /** 단건 조회(수정 폼용) */
    public Product findById(Connection conn, String productId) throws SQLException {
        String sql = SELECT_BASE + " WHERE p.product_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                        rs.getString("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getInt("price"),
                        rs.getString("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getString("product_status"),
                        rs.getString("row_status")
                    );
                }
                return null;
            }
        }
    }

    /** 단건 수정 */
    public int update(Connection conn, Product p) throws SQLException {
        String sql = "UPDATE product " +
                     "SET product_name = ?, category = ?, price = ?, supplier_id = ?, product_status = ? " +
                     "WHERE product_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getProduct_name());
            pstmt.setString(2, p.getCategory());
            pstmt.setInt(3, p.getPrice());
            pstmt.setString(4, p.getSupplier_id());
            pstmt.setString(5, p.getProduct_status());
            pstmt.setString(6, p.getProduct_id());
            return pstmt.executeUpdate();
        }
    }
}
