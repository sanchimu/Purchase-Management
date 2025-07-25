package vo;

import java.util.Date;

public class OrderSheet {
	String order_id;
	String request_id;
	String supplier_id;
	Date order_date;
	String order_status;

	public OrderSheet() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OrderSheet(String order_id, String request_id, String supplier_id, Date order_date, String order_status) {
		super();
		this.order_id = order_id;
		this.request_id = request_id;
		this.supplier_id = supplier_id;
		this.order_date = order_date;
		this.order_status = order_status;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getRequest_id() {
		return request_id;
	}

	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}

	public String getSupplier_id() {
		return supplier_id;
	}

	public void setSupplier_id(String supplier_id) {
		this.supplier_id = supplier_id;
	}

	public Date getOrder_date() {
		return order_date;
	}

	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}

	public String getOrder_status() {
		return order_status;
	}

	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}

}
