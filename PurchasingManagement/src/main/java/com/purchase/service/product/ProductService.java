package com.purchase.service.product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

import com.purchase.dao.product.ProductDao;
import com.purchase.vo.Product;

import jdbc.connection.ConnectionProvider;

public class ProductService {
    private final ProductDao productDao = new ProductDao();

    // 업무상태 세트
    private static final List<String> PRODUCT_STATUS =
            Arrays.asList("정상판매", "일시품절", "예약판매", "판매보류", "단종");

    public List<String> getProductStatusList() { return PRODUCT_STATUS; }

    /** 등록 */
    public void addProduct(Product product) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            productDao.insert(conn, product);
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

    /** 카테고리 목록 */
    public List<String> getCategoryList() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return productDao.getCategoryList(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 단건 업무상태 변경 */
    public void updateProductStatus(Product product) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            productDao.updateProductStatus(conn, product);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 업무상태 일괄 변경 */
    public int updateProductStatusBulk(Map<String, String> statusById) {
        if (statusById == null || statusById.isEmpty()) return 0;
        // 화이트리스트 검증
        for (String st : statusById.values()) {
            if (!PRODUCT_STATUS.contains(st)) {
                throw new IllegalArgumentException("허용되지 않은 상태: " + st);
            }
        }
        try (Connection conn = ConnectionProvider.getConnection()) {
            // 간단 루프(배치 쓰고 싶으면 DAO에 전용 메서드 만들어도 됨)
            int cnt = 0;
            for (Map.Entry<String,String> e : statusById.entrySet()) {
                Product p = new Product();
                p.setProduct_id(e.getKey());
                p.setProduct_status(e.getValue());
                cnt += productDao.updateProductStatus(conn, p);
            }
            return cnt;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 단건 조회/수정 */
    public Product findById(String productId) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return productDao.findById(conn, productId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int update(Product p) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return productDao.update(conn, p);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
