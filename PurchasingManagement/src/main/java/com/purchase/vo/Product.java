package com.purchase.vo;

/**
 * [KO] 상품 VO
 *  - row_status: 'A' = 진행중, 'X' = 중단 (공통 표시/숨김 스위치)
 * [JA] 商品 VO
 *  - row_status: 'A' = 進行中, 'X' = 中断（共通の表示切替スイッチ）
 */
public class Product {
	private String product_id;
	private String product_name;
	private String category;
	private int price;
	private String supplier_id;
	private String product_status;

	// 공통 스위치 (A=진행중, X=중단)
	private String row_status;

	public Product() {
		super();
	}

	public Product(String product_id, String product_name, String category, int price, String supplier_id, String product_status) {
		super();
		this.product_id = product_id;
		this.product_name = product_name;
		this.category = category;
		this.price = price;
		this.supplier_id = supplier_id;
		this.product_status = product_status;
	}

	// row_status까지 포함한 오버로드 생성자 (필요시 사용)
	public Product(String product_id, String product_name, String category, int price, String supplier_id, String product_status, String row_status) {
		super();
		this.product_id = product_id;
		this.product_name = product_name;
		this.category = category;
		this.price = price;
		this.supplier_id = supplier_id;
		this.product_status = product_status;
		this.row_status = row_status;
	}

	public String getProduct_status() {
		return product_status;
	}

	public void setProduct_status(String product_status) {
		this.product_status = product_status;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getSupplier_id() {
		return supplier_id;
	}

	public void setSupplier_id(String supplier_id) {
		this.supplier_id = supplier_id;
	}

	/** 'A' or 'X' */
	public String getRow_status() {
		return row_status;
	}

	public void setRow_status(String row_status) {
		this.row_status = row_status;
	}
}
