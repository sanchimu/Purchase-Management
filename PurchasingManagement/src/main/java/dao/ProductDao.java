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
			int seqNum = 0;
			pstmt = conn.prepareStatement("select product_seq.nextval from dual");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				seqNum = rs.getInt(1);
			}
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);

			// 2. product_id 만들기: "P" + 3자리 숫자 (ex. P001)
			String productId = String.format("P%03d", seqNum);

			
			pstmt=conn.prepareStatement("insert into product values (?,?,?,?,?)");
			pstmt.setString(1, product.getProduct_id());
			pstmt.setString(2, product.getProduct_name());
			pstmt.setString(3, product.getCategory());
			pstmt.setInt(4, product.getPrice());
			pstmt.setString(5, product.getSupplier_id());
			pstmt.executeUpdate();
			int insertedCount = pstmt.executeUpdate();

			if (insertedCount > 0) {
				// 방금 삽입된 product_id 가져오기
				stmt = conn.createStatement();
				rs = stmt.executeQuery(
					"select * from (select product_id from product order by product_id desc) where rownum = 1"
				);
				if (rs.next()) {
					int newId = rs.getInt("product_id");
					return new Product(
						product.getProduct_id(),
						product.getProduct_name(),
						product.getCategory(),
						product.getPrice(),
						product.getSupplier_id()
					);
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
