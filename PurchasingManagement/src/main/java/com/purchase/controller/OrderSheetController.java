package com.purchase.controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.purchase.vo.OrderSheet;

public class OrderSheetController {
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    
    // 데이터베이스 연결 설정
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:XE", 
                "username", 
                "password"
            );
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle JDBC Driver not found", e);
        }
    }
    
    // 발주서 등록 처리
    public String processOrderSheetInsert(String orderId, String requestId, String supplierId, 
                                        String orderDate, String orderStatus) {
        String sql = "INSERT INTO order_sheet (order_id, request_id, supplier_id, order_date, order_status) VALUES (?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?)";
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, orderId);
            pstmt.setString(2, requestId);
            pstmt.setString(3, supplierId);
            pstmt.setString(4, orderDate);
            pstmt.setString(5, orderStatus);
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                return "SUCCESS:발주서가 성공적으로 등록되었습니다.";
            } else {
                return "ERROR:발주서 등록에 실패했습니다.";
            }
            
        } catch (SQLException e) {
            return "ERROR:데이터베이스 오류 - " + e.getMessage();
        } finally {
            closeResources();
        }
    }
    
    // 발주서 목록 조회
    public List<OrderSheet> getOrderSheetList() {
        List<OrderSheet> orderList = new ArrayList<>();
        String sql = "SELECT * FROM order_sheet ORDER BY order_date DESC";
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                OrderSheet order = new OrderSheet();
                order.setOrder_id(rs.getString("order_id"));
                order.setRequest_id(rs.getString("request_id"));
                order.setSupplier_id(rs.getString("supplier_id"));
                order.setOrder_date(rs.getDate("order_date"));
                order.setOrder_status(rs.getString("order_status"));
                orderList.add(order);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        
        return orderList;
    }
    
    // 발주서 상태 변경 처리
    public String processOrderSheetStatusUpdate(String orderId, String newStatus) {
        String sql = "UPDATE order_sheet SET order_status = ? WHERE order_id = ?";
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newStatus);
            pstmt.setString(2, orderId);
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                return "SUCCESS:발주서 상태가 성공적으로 변경되었습니다.";
            } else {
                return "ERROR:해당 발주서를 찾을 수 없습니다.";
            }
            
        } catch (SQLException e) {
            return "ERROR:데이터베이스 오류 - " + e.getMessage();
        } finally {
            closeResources();
        }
    }
    
    // 발주서 삭제 처리
    public String processOrderSheetDelete(String orderId) {
        String sql = "DELETE FROM order_sheet WHERE order_id = ?";
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, orderId);
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                return "SUCCESS:발주서가 성공적으로 삭제되었습니다.";
            } else {
                return "ERROR:해당 발주서를 찾을 수 없습니다.";
            }
            
        } catch (SQLException e) {
            return "ERROR:데이터베이스 오류 - " + e.getMessage();
        } finally {
            closeResources();
        }
    }
    
    // 구매요청 ID로 발주서 검색
    public List<OrderSheet> searchOrderSheetsByRequestId(String requestId) {
        List<OrderSheet> orderList = new ArrayList<>();
        String sql = "SELECT * FROM order_sheet WHERE request_id = ? ORDER BY order_date DESC";
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, requestId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                OrderSheet order = new OrderSheet();
                order.setOrder_id(rs.getString("order_id"));
                order.setRequest_id(rs.getString("request_id"));
                order.setSupplier_id(rs.getString("supplier_id"));
                order.setOrder_date(rs.getDate("order_date"));
                order.setOrder_status(rs.getString("order_status"));
                orderList.add(order);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        
        return orderList;
    }
    
    // 발주서 상세 조회
    public OrderSheet getOrderSheetDetail(String orderId) {
        OrderSheet order = null;
        String sql = "SELECT * FROM order_sheet WHERE order_id = ?";
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, orderId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                order = new OrderSheet();
                order.setOrder_id(rs.getString("order_id"));
                order.setRequest_id(rs.getString("request_id"));
                order.setSupplier_id(rs.getString("supplier_id"));
                order.setOrder_date(rs.getDate("order_date"));
                order.setOrder_status(rs.getString("order_status"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        
        return order;
    }
    
    // 발주서 통계 조회
    public String getOrderSheetStatistics() {
        StringBuilder stats = new StringBuilder();
        String sql = "SELECT order_status, COUNT(*) as count FROM order_sheet GROUP BY order_status";
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            stats.append("발주서 상태별 통계:\n");
            while (rs.next()) {
                stats.append(rs.getString("order_status"))
                     .append(": ")
                     .append(rs.getInt("count"))
                     .append("건\n");
            }
            
        } catch (SQLException e) {
            return "통계 조회 중 오류 발생: " + e.getMessage();
        } finally {
            closeResources();
        }
        
        return stats.toString();
    }
    
    // 리소스 정리
    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}