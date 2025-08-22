package com.purchase.vo;

import java.util.Date;

public class PurchaseRequest {
	private String request_id;
	private String product_id;
	private Integer quantity;
	private Date request_date;
	private String requester_name;

	// 업무상태
	// 業務状態（例：受付/検討中/承認/差し戻し/取り消し/完了）
	private String request_status;

	// 표시상태 (A=활성, X=중단)
	// 表示状態（A=有効、X=中断）
	private String row_status;

	// 공급업체ID（product와 JOIN）
	// サプライヤーID（productとJOIN）
	private String supplier_id;

	public PurchaseRequest() {
	}

	public PurchaseRequest(String request_id, String product_id, Integer quantity, Date request_date,
			String requester_name) {
		this.request_id = request_id;
		this.product_id = product_id;
		this.quantity = quantity;
		this.request_date = request_date;
		this.requester_name = requester_name;
	}

	// 상태,표시상태까지 포함한 생성자
	// 状態、表示状態まで含むコンストラクタ
	public PurchaseRequest(String request_id, String product_id, Integer quantity, Date request_date,
			String requester_name, String request_status, String row_status) {
		this.request_id = request_id;
		this.product_id = product_id;
		this.quantity = quantity;
		this.request_date = request_date;
		this.requester_name = requester_name;
		this.request_status = request_status;
		this.row_status = row_status;
	}

	public String getRequest_id() {
		return request_id;
	}

	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Date getRequest_date() {
		return request_date;
	}

	public void setRequest_date(Date request_date) {
		this.request_date = request_date;
	}

	public String getRequester_name() {
		return requester_name;
	}

	public void setRequester_name(String requester_name) {
		this.requester_name = requester_name;
	}

	public String getRequest_status() {
		return request_status;
	}

	public void setRequest_status(String request_status) {
		this.request_status = request_status;
	}

	public String getRow_status() {
		return row_status;
	}

	public void setRow_status(String row_status) {
		this.row_status = row_status;
	}

	public String getSupplier_id() {
		return supplier_id;
	}

	public void setSupplier_id(String supplier_id) {
		this.supplier_id = supplier_id;
	}
}
