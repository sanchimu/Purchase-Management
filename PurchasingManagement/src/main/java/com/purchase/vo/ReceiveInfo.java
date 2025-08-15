package com.purchase.vo;

import java.util.Date;

public class ReceiveInfo {
    private String receive_id;
    private String order_id;
    private String product_id;
    private int quantity;
    private Date receive_date;

    // 업무상태(검수중/정상/입고 취소/반품 처리 등)
    private String receive_status;

    // 반품 가능 수량(기존 필드 유지)
    private int available_to_return;

    public ReceiveInfo() {}

    public ReceiveInfo(String receive_id, String order_id, String product_id,
                       int quantity, Date receive_date, String receive_status) {
        this.receive_id = receive_id;
        this.order_id = order_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.receive_date = receive_date;
        this.receive_status = receive_status;
    }

    public String getReceive_id() { return receive_id; }
    public void setReceive_id(String receive_id) { this.receive_id = receive_id; }

    public String getOrder_id() { return order_id; }
    public void setOrder_id(String order_id) { this.order_id = order_id; }

    public String getProduct_id() { return product_id; }
    public void setProduct_id(String product_id) { this.product_id = product_id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Date getReceive_date() { return receive_date; }
    public void setReceive_date(Date receive_date) { this.receive_date = receive_date; }

    public String getReceive_status() { return receive_status; }
    public void setReceive_status(String receive_status) { this.receive_status = receive_status; }

    public int getAvailable_to_return() { return available_to_return; }
    public void setAvailable_to_return(int available_to_return) { this.available_to_return = available_to_return; }
}
