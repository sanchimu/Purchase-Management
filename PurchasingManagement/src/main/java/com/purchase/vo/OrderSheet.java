package com.purchase.vo;

import java.util.Date;

/**
 * [KO] 발주서 VO
 *  - order_status : 업무상태 (예: 요청/발송/완료/취소/보류)
 *  - row_status   : 공통 표시스위치 ('A'=진행중, 'X'=중단)
 */
public class OrderSheet {
    private String order_id;
    private String request_id;
    private String supplier_id;
    private Date   order_date;

    // 업무상태
    private String order_status;

    // 공통 표시상태(A/X)
    private String row_status;

    public OrderSheet() { }

    // 기존 호환(표시상태 제외)
    public OrderSheet(String order_id, String request_id, String supplier_id,
                      Date order_date, String order_status) {
        this.order_id = order_id;
        this.request_id = request_id;
        this.supplier_id = supplier_id;
        this.order_date = order_date;
        this.order_status = order_status;
    }

    // 전체 필드 포함
    public OrderSheet(String order_id, String request_id, String supplier_id,
                      Date order_date, String order_status, String row_status) {
        this.order_id = order_id;
        this.request_id = request_id;
        this.supplier_id = supplier_id;
        this.order_date = order_date;
        this.order_status = order_status;
        this.row_status = row_status;
    }

    public String getOrder_id() { return order_id; }
    public void setOrder_id(String order_id) { this.order_id = order_id; }

    public String getRequest_id() { return request_id; }
    public void setRequest_id(String request_id) { this.request_id = request_id; }

    public String getSupplier_id() { return supplier_id; }
    public void setSupplier_id(String supplier_id) { this.supplier_id = supplier_id; }

    public Date getOrder_date() { return order_date; }
    public void setOrder_date(Date order_date) { this.order_date = order_date; }

    /** 업무상태 */
    public String getOrder_status() { return order_status; }
    public void setOrder_status(String order_status) { this.order_status = order_status; }

    /** 'A' or 'X' */
    public String getRow_status() { return row_status; }
    public void setRow_status(String row_status) { this.row_status = row_status; }
}
