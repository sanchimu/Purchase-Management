package com.purchase.vo;

/**
 * [KO] 공급업체 VO
 *  - supplier_status : 업무상태 (예: 활성/거래 중지/폐업/휴면)
 *  - row_status      : 공통 표시스위치 ('A'=진행중, 'X'=중단)
 */
public class SupplierInfo {
    private String supplier_id;
    private String supplier_name;
    private String contact_number;
    private String address;

    // 업무상태
    private String supplier_status;

    // 공통 표시상태(A/X)
    private String row_status;

    public SupplierInfo() { }

    // 기존 호환(업무/표시상태 제외)
    public SupplierInfo(String supplier_id, String supplier_name, String contact_number, String address) {
        this.supplier_id = supplier_id;
        this.supplier_name = supplier_name;
        this.contact_number = contact_number;
        this.address = address;
    }

    // 전체 필드 포함
    public SupplierInfo(String supplier_id, String supplier_name, String contact_number, String address,
                        String supplier_status, String row_status) {
        this.supplier_id = supplier_id;
        this.supplier_name = supplier_name;
        this.contact_number = contact_number;
        this.address = address;
        this.supplier_status = supplier_status;
        this.row_status = row_status;
    }

    public String getSupplier_id() { return supplier_id; }
    public void setSupplier_id(String supplier_id) { this.supplier_id = supplier_id; }

    public String getSupplier_name() { return supplier_name; }
    public void setSupplier_name(String supplier_name) { this.supplier_name = supplier_name; }

    public String getContact_number() { return contact_number; }
    public void setContact_number(String contact_number) { this.contact_number = contact_number; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    /** 업무상태 */
    public String getSupplier_status() { return supplier_status; }
    public void setSupplier_status(String supplier_status) { this.supplier_status = supplier_status; }

    /** 'A' or 'X' */
    public String getRow_status() { return row_status; }
    public void setRow_status(String row_status) { this.row_status = row_status; }
}
