// PurchaseOrderDAO.java
package com.purchase.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.purchase.util.DBConnection;
import com.purchase.vo.OrderSheet;
import com.purchase.vo.Product;
import com.purchase.vo.PurchaseRequest;
import com.purchase.vo.PurchaseRequestItem;

public class PurchaseOrderDAO {
    
    // 발주 요청 등록
    public boolean insertPurchaseRequest(PurchaseRequest request) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean result = false;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO purchase_request (request_id, requester_name, request_date) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, request.getRequest_id());
            pstmt.setString(2, request.getRequester_name());
            pstmt.setDate(3, new java.sql.Date(request.getRequest_date().getTime()));
            
            int rowsAffected = pstmt.executeUpdate();
            result = rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    // 발주서 등록
    public boolean insertOrderSheet(OrderSheet order) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean result = false;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO order_sheet (order_id, request_id, supplier_id, order_date, order_status) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, order.getOrder_id());
            pstmt.setString(2, order.getRequest_id());
            pstmt.setString(3, order.getSupplier_id());
            pstmt.setDate(4, new java.sql.Date(order.getOrder_date().getTime()));
            pstmt.setString(5, order.getOrder_status());
            
            int rowsAffected = pstmt.executeUpdate();
            result = rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    // 발주 요청 상품 등록
    public boolean insertPurchaseRequestItem(PurchaseRequestItem item) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean result = false;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO purchase_request_item (request_id, product_id, quantity) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, item.getRequestId());
            pstmt.setString(2, item.getProductId());
            pstmt.setInt(3, item.getQuantity());
            
            int rowsAffected = pstmt.executeUpdate();
            result = rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    // 상품 등록 (없는 경우)
    public boolean insertProduct(Product product) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean result = false;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO product (product_id, product_name, category, price, supplier_id) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, product.getProduct_id());;
            pstmt.setString(2, product.getProduct_name());
            pstmt.setString(3, product.getCategory());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setString(5, product.getSupplier_id());
            
            int rowsAffected = pstmt.executeUpdate();
            result = rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    // 상품 존재 여부 확인
    public boolean isProductExists(String productId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean exists = false;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM product WHERE product_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, productId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return exists;
    }
    
    // 최근 발주서 목록 조회
    public List<OrderSheet> getRecentOrders(int limit) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<OrderSheet> orders = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM (SELECT * FROM order_sheet ORDER BY order_date DESC) WHERE ROWNUM <= ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                OrderSheet order = new OrderSheet();
                order.setOrder_id(rs.getString("order_id"));
                order.setRequest_id(rs.getString("request_id"));
                order.setSupplier_id(rs.getString("supplier_id"));
                order.setOrder_date(rs.getDate("order_date"));
                order.setOrder_status(rs.getString("order_status"));
                orders.add(order);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return orders;
    }
    
    // ID 생성 메소드
    public String generateRequestId() {
        return "PR" + System.currentTimeMillis();
    }
    
    public String generateOrderId() {
        return "OR" + System.currentTimeMillis();
    }
}