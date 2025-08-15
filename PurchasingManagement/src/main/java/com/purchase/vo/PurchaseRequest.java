package com.purchase.vo;

import java.util.Date;

/**
 * [KO] 구매요청 VO
 *  - row_status: 'A' = 진행중, 'X' = 중단 (공통 표시/숨김 스위치)
 * [JA] 購買申請 VO
 *  - row_status: 'A' = 進行中, 'X' = 中断（共通の表示切替スイッチ）
 */
public class PurchaseRequest {
    String request_id;
    String product_id;
    int quantity;
    Date request_date;
    String requester_name;

    // 공통 스위치 (A=진행중, X=중단)
    String row_status;

    public PurchaseRequest() {
        super();
    }

    public PurchaseRequest(String request_id, String product_id, int quantity, Date request_date, String requester_name) {
        super();
        this.request_id = request_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.request_date = request_date;
        this.requester_name = requester_name;
    }

    // row_status까지 포함한 오버로드 생성자 (필요시 사용)
    public PurchaseRequest(String request_id, String product_id, int quantity, Date request_date,
                           String requester_name, String row_status) {
        super();
        this.request_id = request_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.request_date = request_date;
        this.requester_name = requester_name;
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

    /** 'A' or 'X' */
    public String getRow_status() {
        return row_status;
    }

    public void setRow_status(String row_status) {
        this.row_status = row_status;
    }
}
