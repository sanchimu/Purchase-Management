package com.purchase.vo;

import java.util.Date;

public class ReturnInfo {
	String return_id;
	String receive_id;
	String product_id;
	int quantity;
	String reason;
	Date return_date;

	public ReturnInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ReturnInfo(String return_id, String receive_id, String product_id, int quantity, String reason,
			Date return_date) {
		super();
		this.return_id = return_id;
		this.receive_id = receive_id;
		this.product_id = product_id;
		this.quantity = quantity;
		this.reason = reason;
		this.return_date = return_date;
	}

	public String getReturn_id() {
		return return_id;
	}

	public void setReturn_id(String return_id) {
		this.return_id = return_id;
	}

	public String getReceive_id() {
		return receive_id;
	}

	public void setReceive_id(String receive_id) {
		this.receive_id = receive_id;
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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getReturn_date() {
		return return_date;
	}

	public void setReturn_date(Date return_date) {
		this.return_date = return_date;
	}

}
