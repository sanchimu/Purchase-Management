package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jdbc.JdbcUtil;
import vo.Product;

public class ProductDao {
	public Product  insert (Connection conn, Product product) throws SQLException {
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			pstmt=conn.prepareStatement("insert into product value (product_seq.nextval,?,?,?,?)");
			pstmt.setString(1, product.getProduct_name());
			pstmt.setString(2, product.getCategory());
			pstmt.setInt(3, product.getPrice());
			pstmt.setString(4, product.getSupplier_id());
			int insertedCount = pstmt.executeUpdate();
			
			if(insertedCount > 0) {
				stmt = conn.createStatement();
				rs = stmt.executeQuery("select * from (select article_no from article order by rownum desc) where rownum =1");
				if(rs.next()) {
					Integer newNum=rs.getInt(1);
					return new Product(product.getProduct_id(), product.getProduct_name(), product.getCategory(), product.getPrice(), product.getSupplier_id());
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
}
