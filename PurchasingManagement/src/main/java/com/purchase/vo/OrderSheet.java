package com.purchase.vo;

import java.util.Date;

public class OrderSheet {
    private String orderId;
    private String requestId;
    private String supplierId;
    private Date orderDate;
    private String orderStatus;
    
    // 기본 생성자
    public OrderSheet() {}
    
    // 매개변수 생성자
    public OrderSheet(String orderId, String requestId, String supplierId, Date orderDate, String orderStatus) {
        this.orderId = orderId;
        this.requestId = requestId;
        this.supplierId = supplierId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
    }
    
    // Getter/Setter
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }
    
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
}