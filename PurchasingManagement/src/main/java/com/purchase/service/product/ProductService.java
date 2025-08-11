package com.purchase.service.product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.purchase.dao.product.ProductDao;
import com.purchase.vo.Product;

import jdbc.connection.ConnectionProvider;

public class ProductService {
	private ProductDao productDao = new ProductDao();

	public void addProduct(Product product) {
		try (Connection conn = ConnectionProvider.getConnection()) {
			productDao.insert(conn, product);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void deleteProduct(String[] productIds) {
		try (Connection conn = ConnectionProvider.getConnection()) {
			for (String id : productIds) {
				productDao.delete(conn, id);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Product> getAllProducts() {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return productDao.selectAll(conn);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Product> getProductsByConditions(Map<String, String> conditions) {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return productDao.selectByConditions(conn, conditions);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getCategoryList() {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return productDao.getCategoryList(conn);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
