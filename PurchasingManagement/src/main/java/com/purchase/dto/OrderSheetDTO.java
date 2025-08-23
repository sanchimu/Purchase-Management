package com.purchase.dto;

import java.util.Date;

/**
 * 발주서 DTO (조인 결과용) / 発注書DTO（結合結果用）
 *
 *   order_sheet (기본) / 発注書（基本）
 *   purchase_request (구매요청) / 購買申請
 *   product (상품) / 商品
 *   supplier_info (공급업체) / 供給業者
 *
 * 화면 표시를 위해 여러 테이블의 필드를 통합 / 画面表示のために複数テーブルの項目を統合
 */
public class OrderSheetDTO {

    /** 발주 ID / 発注ID */
    private String order_id;

    /** 발주일 / 発注日 */
    private Date order_date;

    /** 업무상태 (発注依頼/承認/発注/一部入荷/完了/取消/保留) */
    private String order_status;

    /** 표시상태 (A=진행, X=중단) / 表示状態（A=進行中, X=停止） */
    private String row_status;

    // ===== Join Key =====
    /** 구매요청 ID / 購買申請ID */
    private String request_id;

    /** 공급업체 ID / 供給業者ID */
    private String supplier_id;

    //  purchase_request 
    /** 상품 ID / 商品ID */
    private String product_id;

    /** 요청수량 / 申請数量 */
    private int request_quantity;

    /** 요청자 / 申請者 */
    private String requester_name;

    //  product & supplier_info 
    /** 상품명 / 商品名 */
    private String product_name;

    /** 공급업체명 / 供給業者名 */
    private String supplier_name;


    //  Getter/Setter 
    public String getOrder_id() { return order_id; }
    public void setOrder_id(String order_id) { this.order_id = order_id; }

    public Date getOrder_date() { return order_date; }
    public void setOrder_date(Date order_date) { this.order_date = order_date; }

    public String getOrder_status() { return order_status; }
    public void setOrder_status(String order_status) { this.order_status = order_status; }

    public String getRow_status() { return row_status; }
    public void setRow_status(String row_status) { this.row_status = row_status; }

    public String getRequest_id() { return request_id; }
    public void setRequest_id(String request_id) { this.request_id = request_id; }

    public String getSupplier_id() { return supplier_id; }
    public void setSupplier_id(String supplier_id) { this.supplier_id = supplier_id; }

    public String getProduct_id() { return product_id; }
    public void setProduct_id(String product_id) { this.product_id = product_id; }

    public int getRequest_quantity() { return request_quantity; }
    public void setRequest_quantity(int request_quantity) { this.request_quantity = request_quantity; }

    public String getRequester_name() { return requester_name; }
    public void setRequester_name(String requester_name) { this.requester_name = requester_name; }

    public String getProduct_name() { return product_name; }
    public void setProduct_name(String product_name) { this.product_name = product_name; }

    public String getSupplier_name() { return supplier_name; }
    public void setSupplier_name(String supplier_name) { this.supplier_name = supplier_name; }
}
