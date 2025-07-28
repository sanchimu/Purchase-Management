package com.purchase.vo;

import java.util.Date;

public class PurchaseRequest {
	String request_id;
	String product_id;
	int quantity;
	Date request_date;
	String requester_name;

	public PurchaseRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PurchaseRequest(String request_id, String product_id, int quantity, Date request_date,
			String requester_name) {
		super();
		this.request_id = request_id;
		this.product_id = product_id;
		this.quantity = quantity;
		this.request_date = request_date;
		this.requester_name = requester_name;
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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
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
}
