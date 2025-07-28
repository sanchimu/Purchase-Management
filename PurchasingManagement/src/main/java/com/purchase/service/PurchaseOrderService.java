// PurchaseOrderService.java
package com.purchase.service;

import java.util.Date;
import java.util.List;
import com.purchase.dao.PurchaseOrderDAO;
import com.purchase.vo.*;

public class PurchaseOrderService {
    private PurchaseOrderDAO dao;
    
    public PurchaseOrderService() {
        this.dao = new PurchaseOrderDAO();
    }
    
    // 발주서 전체 등록 처리 (트랜잭션)
    public boolean createPurchaseOrder(String requesterName, Date requestDate, String supplierId, 
                                     Date orderDate, List<PurchaseRequestItem> items) {
        
        try {
            // 1. 발주 요청 등록
            String requestId = dao.generateRequestId();
            PurchaseRequest request = new PurchaseRequest(requestId, requesterName, requestDate);
            
            if (!dao.insertPurchaseRequest(request)) {
                return false;
            }
            
            // 2. 발주서 등록
            String orderId = dao.generateOrderId();
            OrderSheet order = new OrderSheet(orderId, requestId, supplierId, orderDate, "요청");
            
            if (!dao.insertOrderSheet(order)) {
                return false;
            }
            
            // 3. 상품 및 발주 요청 상품 등록
            for (PurchaseRequestItem item : items) {
                // 상품이 없으면 등록
                if (!dao.isProductExists(item.getProductId())) {
                    Product product = item.getProduct();
                    if (product != null) {
                        if (!dao.insertProduct(product)) {
                            return false;
                        }
                    }
                }
                
                // 발주 요청 상품 등록
                item.setRequestId(requestId);
                if (!dao.insertPurchaseRequestItem(item)) {
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 최근 발주서 목록 조회
    public List<OrderSheet> getRecentOrders(int limit) {
        return dao.getRecentOrders(limit);
    }
}