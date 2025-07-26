package com.purchase.vo;

public class PurchaseRequestItem {
    private String requestId;
    private String productId;
    private int quantity;
    private Product product; // 조인을 위한 Product 객체
    
    // 기본 생성자
    public PurchaseRequestItem() {}
    
    // 매개변수 생성자
    public PurchaseRequestItem(String requestId, String productId, int quantity) {
        this.requestId = requestId;
        this.productId = productId;
        this.quantity = quantity;
    }
    
    // Getter/Setter
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}