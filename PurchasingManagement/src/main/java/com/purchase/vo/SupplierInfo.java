package com.purchase.vo;

/**
 * [KO] 공급업체 VO
 *  - row_status: 'A' = 진행중, 'X' = 중단 (공통 표시/숨김 스위치)
 * [JA] 供給業者 VO
 *  - row_status: 'A' = 進行中, 'X' = 中断（共通の表示切替スイッチ）
 */
public class SupplierInfo {
    String supplier_id;
    String supplier_name;
    String contact_number;
    String address;

    // 공통 스위치 (A=진행중, X=중단)
    String row_status;

    public SupplierInfo() {
        super();
    }

    public SupplierInfo(String supplier_id, String supplier_name, String contact_number, String address) {
        super();
        this.supplier_id = supplier_id;
        this.supplier_name = supplier_name;
        this.contact_number = contact_number;
        this.address = address;
    }

    // row_status까지 포함한 오버로드 생성자 (필요시 사용)
    public SupplierInfo(String supplier_id, String supplier_name, String contact_number, String address, String row_status) {
        super();
        this.supplier_id = supplier_id;
        this.supplier_name = supplier_name;
        this.contact_number = contact_number;
        this.address = address;
        this.row_status = row_status;
    }

    public String getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(String supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /** 'A' or 'X' */
    public String getRow_status() {
        return row_status;
    }

    public void setRow_status(String row_status) {
        this.row_status = row_status;
    }
}
