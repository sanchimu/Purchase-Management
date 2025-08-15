package com.purchase.service.product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;   // ✅ 추가

import com.purchase.dao.product.ProductDao;
import com.purchase.vo.Product;

import jdbc.connection.ConnectionProvider;

public class ProductService {
    private ProductDao productDao = new ProductDao();

    // ✅ 실무형 상품 상태 세트 (업무상태용)
    // 정상판매 / 일시품절 / 예약판매 / 판매보류 / 단종
    private static final List<String> PRODUCT_STATUS =
            Arrays.asList("정상판매", "일시품절", "예약판매", "판매보류", "단종");

    /** 화면 드롭다운 등에 사용할 상태 세트 */
    public List<String> getProductStatusList() {
        return PRODUCT_STATUS;
    }

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

    /** 기존 전체 조회 (변경 없음) */
    public List<Product> getAllProducts() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return productDao.selectAll(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 조건 조회 (변경 없음) */
    public List<Product> getProductsByConditions(Map<String, String> conditions) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return productDao.selectByConditions(conn, conditions);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 카테고리 리스트 (변경 없음) */
    public List<String> getCategoryList() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return productDao.getCategoryList(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateProductStatus(Product product) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            productDao.updateProductStatus(conn, product);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================
    // ✅ 신규: 표시여부 필터 지원
    // =========================

    /**
     * includeHidden=false  → row_status = 'A'만 반환
     * includeHidden=true   → A, X 모두 반환
     * (row_status가 null이면 안전하게 'A' 취급)
     */
    public List<Product> getProducts(boolean includeHidden) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            List<Product> all = productDao.selectAll(conn); // DAO 변경 없이 재사용
            if (includeHidden) return all;
            return all.stream()
                      .filter(p -> {
                          String rs = p.getRow_status();
                          return rs == null || "A".equalsIgnoreCase(rs);
                      })
                      .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 조건조회 + 표시여부 필터 조합 버전 (필요 시 사용)
     */
    public List<Product> getProductsByConditions(Map<String, String> conditions, boolean includeHidden) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            List<Product> list = productDao.selectByConditions(conn, conditions);
            if (includeHidden) return list;
            return list.stream()
                       .filter(p -> {
                           String rs = p.getRow_status();
                           return rs == null || "A".equalsIgnoreCase(rs);
                       })
                       .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
