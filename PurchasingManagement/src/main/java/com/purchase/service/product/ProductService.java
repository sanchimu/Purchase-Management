package com.purchase.service.product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
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
	
	public void updateProductStatus(Product product) {
	    try (Connection conn = ConnectionProvider.getConnection()) {
	        productDao.updateProductStatus(conn, product); // DAO에 conn 전달
	    } catch (SQLException e) {
	        throw new RuntimeException(e);
	    }
	}
	
    /** 조건조회(+includeHidden) */
    public List<Product> getProductsByConditions(Map<String, String> conditions, boolean includeHidden) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            Map<String,String> cond = new HashMap<>();
            if (conditions != null) cond.putAll(conditions);
            cond.put("includeHidden", includeHidden ? "1" : "0");
            return productDao.selectByConditions(conn, cond);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Product getProductById(String productId) {
    	try (Connection conn = ConnectionProvider.getConnection()) {
	        return productDao.getProductById(conn, productId); // DAO에 conn 전달
	    } catch (SQLException e) {
	        throw new RuntimeException(e);
	    }
    }
    
    public int modifyProduct(Product product) {
    	try (Connection conn = ConnectionProvider.getConnection()) {
    		return productDao.modifyProduct(conn, product); // DAO에 conn 전달
    	} catch (SQLException e) {
    		throw new RuntimeException(e);
    	}
    }
}
