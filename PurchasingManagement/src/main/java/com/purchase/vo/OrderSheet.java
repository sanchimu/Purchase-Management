package com.purchase.vo;

import java.util.Date;

/**
 * [KO] 발주서 VO
 *  - row_status: 'A' = 진행중, 'X' = 중단 (공통 표시/숨김 스위치)
 * [JA] 発注書 VO
 *  - row_status: 'A' = 進行中, 'X' = 中断（共通の表示切替スイッチ）
 */
public class OrderSheet {
    String order_id;
    String request_id;
    String supplier_id;
    Date order_date;
    String order_status;

    // 공통 스위치 (A=진행중, X=중단)
    String row_status;

    public OrderSheet() {
        super();
    }

    public OrderSheet(String order_id, String request_id, String supplier_id, Date order_date, String order_status) {
        super();
        this.order_id = order_id;
        this.request_id = request_id;
        this.supplier_id = supplier_id;
        this.order_date = order_date;
        this.order_status = order_status;
    }

    // row_status까지 포함한 오버로드 생성자 (필요시 사용)
    public OrderSheet(String order_id, String request_id, String supplier_id, Date order_date,
                      String order_status, String row_status) {
        super();
        this.order_id = order_id;
        this.request_id = request_id;
        this.supplier_id = supplier_id;
        this.order_date = order_date;
        this.order_status = order_status;
        this.row_status = row_status;
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

    /** 'A' or 'X' */
    public String getRow_status() {
        return row_status;
    }

    public void setRow_status(String row_status) {
        this.row_status = row_status;
    }
}
