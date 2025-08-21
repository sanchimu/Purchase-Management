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

			pstmt = conn.prepareStatement("insert into product values (?,?,?,?,?,?)");
			pstmt.setString(1, product.getProduct_id());
			pstmt.setString(2, product.getProduct_name());
			pstmt.setString(3, product.getCategory());
			pstmt.setInt(4, product.getPrice());
			pstmt.setString(5, product.getSupplier_id());
			pstmt.setString(6, product.getProduct_status());
			int insertedCount = pstmt.executeUpdate();

			if (insertedCount > 0) {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(
						"select * from (select product_id from product order by product_id desc) where rownum = 1");
				if (rs.next()) {
					return new Product(product.getProduct_id(), product.getProduct_name(), product.getCategory(),
							product.getPrice(), product.getSupplier_id(), product.getProduct_status());
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

	public int delete(Connection conn, String pno) throws SQLException {
		try (PreparedStatement pstmt = conn.prepareStatement("delete from product where PRODUCT_ID = ?")) {
			pstmt.setString(1, pno);
			return pstmt.executeUpdate();
		}
	}

	public List<Product> selectAll(Connection conn) throws SQLException {
		String sql = "SELECT p.product_id, p.product_name, p.category, p.price, p.supplier_id, s.supplier_name, p.product_status"
				+ " FROM product p JOIN supplier_info s ON p.supplier_id = s.supplier_id ORDER BY p.product_id";
		List<Product> list = new ArrayList<>();
		try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				Product p = new Product(rs.getString("product_id"), rs.getString("product_name"),
						rs.getString("category"), rs.getInt("price"), rs.getString("supplier_id"), rs.getString("supplier_name"), rs.getString("product_status"));
				list.add(p);
			}
		}
		return list;
	}

	public List<Product> selectByConditions(Connection conn, Map<String, String> conditions) throws SQLException {
		StringBuilder sql = new StringBuilder("SELECT * FROM product WHERE 1=1");
		List<Object> params = new ArrayList<>();

		if (conditions.get("product_id") != null) {
			sql.append(" AND UPPER(product_id) LIKE UPPER(?)");
			params.add("%" + conditions.get("product_id") + "%");
		}
		if (conditions.get("product_name") != null) {
			sql.append(" AND UPPER(product_name) LIKE UPPER(?)");
			params.add("%" + conditions.get("product_name") + "%");
		}
		if (conditions.get("category") != null) {
			sql.append(" AND UPPER(category) LIKE  UPPER(?)");
			params.add("%" + conditions.get("category") + "%");
		}
		if (conditions.get("supplier_id") != null) {
			sql.append(" AND UPPER(supplier_id) LIKE UPPER(?)");
			params.add("%" + conditions.get("supplier_id") + "%");
		}
		sql.append(" ORDER BY product_id");

		try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(i + 1, params.get(i));
			}
			try (ResultSet rs = pstmt.executeQuery()) {
				List<Product> list = new ArrayList<>();
				while (rs.next()) {
					Product p = new Product(rs.getString("product_id"), rs.getString("product_name"),
							rs.getString("category"), rs.getInt("price"), rs.getString("supplier_id"), rs.getString("product_status"));
					list.add(p);
				}
				return list;
			}
		}
	}

	
	  public List<String> getCategoryList(Connection conn) {
	  
	  List<String> categoryList = new ArrayList<>();
	  
	  try (PreparedStatement pstmt = conn.
	  prepareStatement("SELECT DISTINCT category FROM product ORDER BY category")){
	  ResultSet rs = pstmt.executeQuery(); while (rs.next()) {
	  categoryList.add(rs.getString("category")); } } catch (Exception e) {
	  e.printStackTrace(); } return categoryList;
	  
	  }
	  
	  public List<String> getSupplierList(Connection conn) {
		  
		  List<String> supplierList = new ArrayList<>();
		  
		  try (PreparedStatement pstmt = conn.
		  prepareStatement("SELECT supplier_id FROM supplier_info ORDER BY supplier_id")){
		  ResultSet rs = pstmt.executeQuery(); 
		  while (rs.next()) {
			  supplierList.add(rs.getString("supplier_id"));
			  System.out.println("DAO - supplier_id: " + rs.getString("supplier_id")); // ← 확인용
			  } 
		  } catch (Exception e) {
				  e.printStackTrace(); 
				  } 
		  return supplierList;
		  
		  }
	  
	 
	  public int updateProductStatus(Connection conn, Product product) throws SQLException {
		  try(PreparedStatement pstmt = conn.prepareStatement("UPDATE PRODUCT SET PRODUCT_STATUS = ? WHERE PRODUCT_ID = ?")){
			  pstmt.setString(1, product.getProduct_status());
			  pstmt.setString(2, product.getProduct_id());
			  return pstmt.executeUpdate();
		  }
	  }
	  
	  public Product getProductById(Connection conn, String productId) {
		  Product product = null;
		    String sql =  "SELECT p.product_id, p.product_name, p.category, p.price, p.supplier_id, s.supplier_name, p.product_status"
					+ " FROM product p JOIN supplier_info s ON p.supplier_id = s.supplier_id WHERE p.product_id = ?";

		    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
		        pstmt.setString(1, productId);
		        try (ResultSet rs = pstmt.executeQuery()) {
		            if (rs.next()) {
		                product = new Product();
		                product.setProduct_id(rs.getString("product_id"));
		                product.setProduct_name(rs.getString("product_name"));
		                product.setCategory(rs.getString("category"));
		                product.setPrice(rs.getInt("price"));
		                product.setSupplier_id(rs.getString("supplier_id"));
		                product.setSupplier_name(rs.getString("supplier_name"));
		                product.setProduct_status(rs.getString("product_status"));
		            }
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    return product;
	  }
	  
	  public int modifyProduct(Connection conn, Product product) throws SQLException { // 반환형이 int인 이유는 반환되는 executeeUpdate가 영향받은 행의 개수를 반환하기 떄문에 개수는 숮자 즉 int로 반환됨
		    String sql = "UPDATE product SET product_name = ?, category = ?, price = ?, supplier_id = ?, product_status = ? WHERE product_id = ?";

		    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
		        pstmt.setString(1, product.getProduct_name());
		        pstmt.setString(2, product.getCategory());
		        pstmt.setInt(3, product.getPrice());
		        pstmt.setString(4, product.getSupplier_id());
		        pstmt.setString(5, product.getProduct_status());
		        pstmt.setString(6, product.getProduct_id()); // WHERE 조건

		        return pstmt.executeUpdate();
		    }
		}
}
