package com.purchase.vo;

/**
 * [JP] 仕入先 VO
 *  - supplier_status : 業務状態 (例: 有効 / 取引停止 / 廃業 / 休眠)
 *  - row_status      : 共通表示スイッチ ('A'=進行中, 'X'=中断)
 */
public class SupplierInfo {
    private String supplier_id;
    private String supplier_name;
    private String contact_number;
    private String address;

    // 業務状態
    private String supplier_status;

    // 共通表示状態(A/X)
    private String row_status;

    public SupplierInfo() { }

    // 既存互換 (業務/表示状態を除く)
    public SupplierInfo(String supplier_id, String supplier_name, String contact_number, String address) {
        this.supplier_id = supplier_id;
        this.supplier_name = supplier_name;
        this.contact_number = contact_number;
        this.address = address;
    }

    // 全フィールドを含む
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

    /** 業務状態 */
    public String getSupplier_status() { return supplier_status; }
    public void setSupplier_status(String supplier_status) { this.supplier_status = supplier_status; }

    /** 'A' または 'X' */
    public String getRow_status() { return row_status; }
    public void setRow_status(String row_status) { this.row_status = row_status; }
}
