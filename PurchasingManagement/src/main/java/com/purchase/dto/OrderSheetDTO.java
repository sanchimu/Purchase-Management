package com.purchase.dto;

import java.util.Date;

public class OrderSheetDTO {
    // order_sheet
    private String order_id;
    private Date   order_date;
    private String order_status;
    private String row_status;

    // join keys
    private String request_id;
    private String supplier_id;

    // purchase_request
    private String product_id;
    private int    request_quantity;
    private String requester_name;

    // product / supplier_info
    private String product_name;
    private String supplier_name;

    // getters & setters
    public String getOrder_id() { return order_id; }
    public void setOrder_id(String order_id) { this.order_id = order_id; }

    public Date getOrder_date() { return order_date; }
    public void setOrder_date(Date order_date) { this.order_date = order_date; }

    public String getOrder_status() { return order_status; }
    public void setOrder_status(String order_status) { this.order_status = order_status; }

    public String getRow_status() { return row_status; }
    public void setRow_status(String row_status) { this.row_status = row_status; }

    public String getRequest_id() { return request_id; }
    public void setRequest_id(String request_id) { this.request_id = request_id; }

    public String getSupplier_id() { return supplier_id; }
    public void setSupplier_id(String supplier_id) { this.supplier_id = supplier_id; }

    public String getProduct_id() { return product_id; }
    public void setProduct_id(String product_id) { this.product_id = product_id; }

    public int getRequest_quantity() { return request_quantity; }
    public void setRequest_quantity(int request_quantity) { this.request_quantity = request_quantity; }

    public String getRequester_name() { return requester_name; }
    public void setRequester_name(String requester_name) { this.requester_name = requester_name; }

    public String getProduct_name() { return product_name; }
    public void setProduct_name(String product_name) { this.product_name = product_name; }

    public String getSupplier_name() { return supplier_name; }
    public void setSupplier_name(String supplier_name) { this.supplier_name = supplier_name; }
}
