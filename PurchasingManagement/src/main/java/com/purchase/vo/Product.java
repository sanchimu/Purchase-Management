package com.purchase.vo;

public class Product {
    private String productId;
    private String productName;
    private String category;
    private double price;
    private String supplierId;
    
    // 기본 생성자
    public Product() {}
    
    // 매개변수 생성자
    public Product(String productId, String productName, String category, double price, String supplierId) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.supplierId = supplierId;
    }
    
    // Getter/Setter
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }
}