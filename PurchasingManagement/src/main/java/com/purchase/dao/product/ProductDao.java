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

			// product_id 만들기: "P" + 3자리 숫자 (ex. P001)
			String productId = String.format("P%03d", seqNum);
			product.setProduct_id(productId);

			pstmt = conn.prepareStatement("insert into product values (?,?,?,?,?)");
			pstmt.setString(1, product.getProduct_id());
			pstmt.setString(2, product.getProduct_name());
			pstmt.setString(3, product.getCategory());
			pstmt.setInt(4, product.getPrice());
			pstmt.setString(5, product.getSupplier_id());
			int insertedCount = pstmt.executeUpdate();

			if (insertedCount > 0) {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(
						"select * from (select product_id from product order by product_id desc) where rownum = 1");
				if (rs.next()) {
					String newId = rs.getString("product_id");
					return new Product(product.getProduct_id(), product.getProduct_name(), product.getCategory(),
							product.getPrice(), product.getSupplier_id());
				}
			}
			return null;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
			JdbcUtil.close(stmt);
			// TODO: handle exception
		}
	}
	public int delete(Connection conn, String pno) throws SQLException{
		try(PreparedStatement pstmt = conn.prepareStatement("delete from product where PRODUCT_ID = ?")){
			pstmt.setString(1, pno);
			return pstmt.executeUpdate();
		}
	}
	
	public List<Product> selectAll(Connection conn) throws SQLException {
	    String sql = "SELECT * FROM product ORDER BY product_id";
	    List<Product> list = new ArrayList<>();
	    try (PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {
	        while (rs.next()) {
	            Product p = new Product(
	                rs.getString("product_id"),
	                rs.getString("product_name"),
	                rs.getString("category"),
	                rs.getInt("price"),
	                rs.getString("supplier_id"));
	            list.add(p);
	        }
	    }
	    return list;
	}
	
	public List<Product> selectByConditions(Connection conn, Map<String, String> conditions) throws SQLException {
	    StringBuilder sql = new StringBuilder("SELECT * FROM product WHERE 1=1");
	    List<Object> params = new ArrayList<>();

	    if (conditions.get("product_id") != null) {
	        sql.append(" AND product_id = ?");
	        params.add(conditions.get("product_id"));
	    }
	    if (conditions.get("product_name") != null) {
	        sql.append(" AND product_name LIKE ?");
	        params.add("%" + conditions.get("product_name") + "%");
	    }
	    if (conditions.get("category") != null) {
	        sql.append(" AND category = ?");
	        params.add(conditions.get("category"));
	    }
	    if (conditions.get("supplier_id") != null) {
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
	                    rs.getString("supplier_id"));
	                list.add(p);
	            }
	            return list;
	        }
	    }
	}
	
}
