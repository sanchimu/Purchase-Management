package com.purchase.vo;

import java.util.Date;

/**
 * [KO] 구매요청 VO
 *  - request_status : 업무상태 (예: 요청/보류/취소/완료)
 *  - row_status     : 공통 표시스위치 ('A'=진행중, 'X'=중단)
 */
public class PurchaseRequest {
    private String request_id;
    private String product_id;
    private Integer    quantity;
    private Date   request_date;
    private String requester_name;

    // 업무상태
    private String request_status;

    // 공통 표시상태(A/X)
    private String row_status;
    
    private String supplier_id;

    public PurchaseRequest() { }

    // 기존 호환(업무/표시상태 제외)
    public PurchaseRequest(String request_id, String product_id, Integer quantity,
                           Date request_date, String requester_name) {
        this.request_id = request_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.request_date = request_date;
        this.requester_name = requester_name;
    }

    // 전체 필드 포함
    public PurchaseRequest(String request_id, String product_id, Integer quantity,
                           Date request_date, String requester_name,
                           String request_status, String row_status) {
        this.request_id = request_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.request_date = request_date;
        this.requester_name = requester_name;
        this.request_status = request_status;
        this.row_status = row_status;
    }

    public String getRequest_id() { return request_id; }
    public void setRequest_id(String request_id) { this.request_id = request_id; }

    public String getProduct_id() { return product_id; }
    public void setProduct_id(String product_id) { this.product_id = product_id; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Date getRequest_date() { return request_date; }
    public void setRequest_date(Date request_date) { this.request_date = request_date; }

    public String getRequester_name() { return requester_name; }
    public void setRequester_name(String requester_name) { this.requester_name = requester_name; }

    /** 업무상태 */
    public String getRequest_status() { return request_status; }
    public void setRequest_status(String request_status) { this.request_status = request_status; }

    /** 'A' or 'X' */
    public String getRow_status() { return row_status; }
    public void setRow_status(String row_status) { this.row_status = row_status; }
    
    public String getSupplier_id() {return supplier_id;}
    public void setSupplier_id(String supplier_id) {this.supplier_id = supplier_id;}
}
