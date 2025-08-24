package com.purchase.service.product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.purchase.dao.product.ProductDao;
import com.purchase.vo.Product;

import jdbc.connection.ConnectionProvider;

public class ProductService {//사실 service는 DB와의 커넥팅을 담당하는중 dao - service - handler순으로 가야 제대로 작동됨
	private ProductDao productDao = new ProductDao();

	public void addProduct(Product product) { 	// 상품 생성용 메서드 (商品作成用メソッド)
		try (Connection conn = ConnectionProvider.getConnection()) { //DB에 연결할 Connection 객체를 생성 (DBに接続するConnectionオブジェクトを作成)
			productDao.insert(conn, product);	//상품 생성에 필요한 productDao의 메서드 (商品の生成に必要なproductDaoのメソッド)
		} catch (SQLException e) {
			throw new RuntimeException(e);		//에러 발생 시 예외 처리 (エラー発生時の例外処理)
		}
	}

	//이하 부분도 위와 같이 Connection을 위해 생성된 메서드들이 전체여서 각 메서드들의 역할만 간단히 기재
	
	public void deleteProduct(String[] productIds) { // 상품 삭제용 메서드 (削除メソッド)
		try (Connection conn = ConnectionProvider.getConnection()) {
			for (String id : productIds) {//이거 요즘 많이 쓰는 새로운 for문인데 앞쪽이 id값, 뒤쪽에 List로 해서 List에 있는 변수를 id에 다 넣을때까지 반복되는 for문임 이거 쓰면 꽤 유용할수 있음
				productDao.delete(conn, id);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Product> getAllProducts() { // 모든 상품 정보를 들고오는 메서드 (すべての商品情報をインポートするメソッド)
		try (Connection conn = ConnectionProvider.getConnection()) {
			return productDao.selectAll(conn);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Product> getProductsByConditions(Map<String, String> conditions) { //상품 상태 정보를 가져오는 메서드 (商品のステータス情報を取得するメソッド)
		try (Connection conn = ConnectionProvider.getConnection()) {
			return productDao.selectByConditions(conn, conditions);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getCategoryList() { //상품의 카테고리 정보들을 가져오는 메서드 (商品のカテゴリ情報を取得するメソッド)
		try (Connection conn = ConnectionProvider.getConnection()) {
			return productDao.getCategoryList(conn);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<String> getSupplierList() { //상품의 공급업체 ID 정보를 가져오는 메서드 (商品のサプライヤーID情報を取得するメソッド)
		try (Connection conn = ConnectionProvider.getConnection()) {
			return productDao.getSupplierList(conn);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void updateProductStatus(Product product) { //상품 정보를 수정 및 갱신하는 메서드 (商品情報を修正および更新するメソッド)
	    try (Connection conn = ConnectionProvider.getConnection()) {
	        productDao.updateProductStatus(conn, product); // DAO에 conn 전달
	    } catch (SQLException e) {
	        throw new RuntimeException(e);
	    }
	}
	
    
    public List<Product> getProductsByConditions(Map<String, String> conditions, boolean includeHidden) { // 조건에 맞는 상품을 찾는 메서드 (条件に合う商品を探すメソッド)
        try (Connection conn = ConnectionProvider.getConnection()) {
            return productDao.selectByConditions(conn, conditions);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Product getProductById(String productId) { //product_Id로 상품정보를 가져오는 메서드 (product_Idで商品情報を取得するメソッド)
    	try (Connection conn = ConnectionProvider.getConnection()) {
	        return productDao.getProductById(conn, productId); // DAO에 conn 전달
	    } catch (SQLException e) {
	        throw new RuntimeException(e);
	    }
    }
    
    public int modifyProduct(Product product) { // 상품 정보를 수정하는 메서드 (商品情報を修正するメソッド)
    	try (Connection conn = ConnectionProvider.getConnection()) {
    		return productDao.modifyProduct(conn, product); // DAO에 conn 전달
    	} catch (SQLException e) {
    		throw new RuntimeException(e);
    	}
    }
}
