package com.purchase.vo;

// 공급업체 정보를 담는 VO 클래스
// 仕入先情報を保持するVOクラス
public class SupplierInfo {
    private String supplier_id;      // 공급업체 ID
                                     // 仕入先ID
    private String supplier_name;    // 공급업체명
                                     // 仕入先名
    private String contact_number;   // 연락처
                                     // 連絡先
    private String address;          // 주소
                                     // 住所
    private String supplier_status;  // 거래 상태 (예: 유효, 거래중지, 폐업, 휴면)
                                     // 取引状態（例: 有効、取引停止、廃業、休眠）
    private String row_status;       // 표시 상태 ('A'=진행중, 'X'=중단)
                                     // 表示状態（'A'=進行中, 'X'=中断）

    public SupplierInfo() { }

    // 상태값 제외한 생성자
    // 状態値を除いたコンストラクタ
    public SupplierInfo(String supplier_id, String supplier_name, String contact_number, String address) {
        this.supplier_id = supplier_id;
        this.supplier_name = supplier_name;
        this.contact_number = contact_number;
        this.address = address;
    }

    // 모든 필드 포함한 생성자
    // 全てのフィールドを含むコンストラクタ
    public SupplierInfo(String supplier_id, String supplier_name, String contact_number, String address,
                        String supplier_status, String row_status) {
        this.supplier_id = supplier_id;
        this.supplier_name = supplier_name;
        this.contact_number = contact_number;
        this.address = address;
        this.supplier_status = supplier_status;
        this.row_status = row_status;
    }

    // ID와 이름만 담는 생성자 (상품 추가 시 드롭다운용 등)
    // IDと名前のみを格納するコンストラクタ（商品追加時のドロップダウン用など）
    public SupplierInfo(String supplier_id, String supplier_name) {
        this.supplier_id = supplier_id;
        this.supplier_name = supplier_name;
    }

    public String getSupplier_id() { return supplier_id; }
    public void setSupplier_id(String supplier_id) { this.supplier_id = supplier_id; }

    public String getSupplier_name() { return supplier_name; }
    public void setSupplier_name(String supplier_name) { this.supplier_name = supplier_name; }

    public String getContact_number() { return contact_number; }
    public void setContact_number(String contact_number) { this.contact_number = contact_number; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getSupplier_status() { return supplier_status; }
    public void setSupplier_status(String supplier_status) { this.supplier_status = supplier_status; }

    public String getRow_status() { return row_status; }
    public void setRow_status(String row_status) { this.row_status = row_status; }
}
