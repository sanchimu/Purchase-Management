package com.purchase.vo;

import java.util.Date;

public class ReceiveInfo {
    private String receive_id;   // 入庫ID
    private String order_id;     // 注文ID
    private String product_id;   // 商品ID
    private int    quantity;     // 入庫数量
    private Date   receive_date; // 入庫日

    // 業務ステータス（検収中 / 正常 / 入庫キャンセル / 返品処理 など）
    private String receive_status;

    // 返品可能数量
    private int    available_to_return;

    // JOINで取得する追加表示用フィールド
    private String product_name;   // 商品名
    private String supplier_name;  // 仕入先名

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

    // 追加された getter/setter
    public String getProduct_name() { return product_name; }
    public void setProduct_name(String product_name) { this.product_name = product_name; }

    public String getSupplier_name() { return supplier_name; }
    public void setSupplier_name(String supplier_name) { this.supplier_name = supplier_name; }
}

