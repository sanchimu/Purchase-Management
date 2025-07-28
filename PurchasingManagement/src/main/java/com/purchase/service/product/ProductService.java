package com.purchase.service.product;

import java.sql.Connection;
import java.sql.SQLException;

import com.purchase.dao.product.ProductDao;
import com.purchase.vo.Product;

import jdbc.connection.ConnectionProvider;

public class ProductService {
	private ProductDao productDao = new ProductDao();
	
	public void addProduct(Product product) {
		try(Connection conn = ConnectionProvider.getConnection()){
			productDao.insert(conn, product);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void deleteProduct(String pno) {
		try(Connection conn = ConnectionProvider.getConnection()){
			
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
