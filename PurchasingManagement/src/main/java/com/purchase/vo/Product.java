package com.purchase.vo;
/**
 * [KO] 상품 VO
 *  - product_status : 업무상태 (예: 정상판매/일시품절/예약판매/판매보류/단종)
 *  - row_status     : 공통 표시스위치 ('A'=진행중, 'X'=중단)
 */
public class Product {
    private String product_id;
    private String product_name;
    private String category;
    private int    price;
    private String supplier_id;
    private String supplier_name;  // 추가: 조인으로 가져올 공급업체명
    // 업무상태
    private String product_status;
    // 공통 표시상태(A/X)
    private String row_status;
    
    public Product() { }
    
    // 기존 호환(표시상태 제외)
    public Product(String product_id, String product_name, String category, int price,
                   String supplier_id, String product_status) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.category = category;
        this.price = price;
        this.supplier_id = supplier_id;
        this.product_status = product_status;
    }
	/*
	 * // 전체 필드 포함 (기존) public Product(String product_id, String product_name,
	 * String category, int price, String supplier_id, String product_status, String
	 * row_status) { this.product_id = product_id; this.product_name = product_name;
	 * this.category = category; this.price = price; this.supplier_id = supplier_id;
	 * this.product_status = product_status; this.row_status = row_status; }
	 */
    
    // 새로운 생성자: supplier_name 포함
    public Product(String product_id, String product_name, String category, int price,
                   String supplier_id, String supplier_name, String product_status, String row_status) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.category = category;
        this.price = price;
        this.supplier_id = supplier_id;
        this.supplier_name = supplier_name;
        this.product_status = product_status;
        this.row_status = row_status;
    }
    
	public Product(String product_id, String product_name, String category, int price, String supplier_id,
			String supplier_name, String product_status) {
		this.product_id = product_id;
		this.product_name = product_name;
		this.category = category;
		this.price = price;
		this.supplier_id = supplier_id;
		this.supplier_name = supplier_name;
		this.product_status = product_status;
	}
    
    public String getProduct_id() { return product_id; }
    public void setProduct_id(String product_id) { this.product_id = product_id; }
    
    public String getProduct_name() { return product_name; }
    public void setProduct_name(String product_name) { this.product_name = product_name; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    
    public String getSupplier_id() { return supplier_id; }
    public void setSupplier_id(String supplier_id) { this.supplier_id = supplier_id; }
    
    // 추가: 공급업체명 getter/setter
    public String getSupplier_name() { return supplier_name; }
    public void setSupplier_name(String supplier_name) { this.supplier_name = supplier_name; }
    
    /** 업무상태 */
    public String getProduct_status() { return product_status; }
    public void setProduct_status(String product_status) { this.product_status = product_status; }
    
    /** 'A' or 'X' */
    public String getRow_status() { return row_status; }
    public void setRow_status(String row_status) { this.row_status = row_status; }
}