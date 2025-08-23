package com.purchase.vo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 발주서 VO / 発注書VO
 *   order_status : 업무상태 (발주依頼/承認/発注/一部入荷/完了/取消/保留)
 *   row_status   : 표시상태 ('A'=진행중/進行中, 'X'=중단/停止)
 *
 *    DB 컬럼명과 1:1 매칭을 위해 필드명은 스네이크케이스 유지
 *   （DBカラムと1:1対応のためフィールド名はスネークケースのまま）
 */
public class OrderSheet implements Serializable {
    private static final long serialVersionUID = 1L;

    //  상태 정의 / 状態定義 
    /** 발주서 업무상태 후보 / 発注書の業務状態候補（DBのCHECK制約と一致） */
    public static final List<String> ORDER_STATUSES = Arrays.asList(
        "発注依頼","承認","発注","一部入荷","完了","取消","保留"
    );

    /** 표시상태 코드 / 表示状態コード */
    public static final String ROW_ACTIVE  = "A"; // 진행중 / 進行中
    public static final String ROW_STOPPED = "X"; // 중단   / 停止

    //  DB 매핑 필드 / DBマッピング用フィールド 
    private String order_id;     // 발주ID / 発注ID
    private String request_id;   // 구매요청ID / 購買申請ID
    private String supplier_id;  // 공급업체ID / 供給業者ID
    private Date   order_date;   // 발주일 / 発注日

    private String order_status; // 업무상태 / 業務状態
    private String row_status;   // 표시상태(A/X) / 表示状態(A/X)

    //  생성자 / コンストラクタ 
    public OrderSheet() { }

    /** 표시상태 제외 기본 생성자 / 表示状態を除く基本コンストラクタ */
    public OrderSheet(String order_id, String request_id, String supplier_id,
                      Date order_date, String order_status) {
        this.order_id = safe(order_id);
        this.request_id = safe(request_id);
        this.supplier_id = safe(supplier_id);
        this.order_date = order_date;
        setOrder_status(order_status); // 트림 및 유효성 옵션 / 余白除去＆任意バリデーション
    }

    /** 전체 필드 생성자 / 全フィールドのコンストラクタ */
    public OrderSheet(String order_id, String request_id, String supplier_id,
                      Date order_date, String order_status, String row_status) {
        this(order_id, request_id, supplier_id, order_date, order_status);
        setRow_status(row_status);
    }

    // ===== Getter/Setter =====
    public String getOrder_id() { return order_id; }
    public void setOrder_id(String order_id) { this.order_id = safe(order_id); }

    public String getRequest_id() { return request_id; }
    public void setRequest_id(String request_id) { this.request_id = safe(request_id); }

    public String getSupplier_id() { return supplier_id; }
    public void setSupplier_id(String supplier_id) { this.supplier_id = safe(supplier_id); }

    public Date getOrder_date() { return order_date; }
    public void setOrder_date(Date order_date) { this.order_date = order_date; }

    /** 업무상태 / 業務状態 */
    public String getOrder_status() { return order_status; }
    public void setOrder_status(String order_status) {
        String v = trimOrNull(order_status);
        // 필요 시 상태 체크 해제/활성화 선택 / 必要なら下記の厳格チェックを有効化
        // if (v != null && !ORDER_STATUSES.contains(v)) {
        //     throw new IllegalArgumentException("invalid order_status: " + v);
        // }
        this.order_status = v;
    }

    /** 표시상태(A/X) / 表示状態(A/X) */
    public String getRow_status() { return row_status; }
    public void setRow_status(String row_status) {
        String v = trimOrNull(row_status);
        // 미설정은 A로 기본화(프로젝트 규약에 맞게 조정) /
        // 未設定はAをデフォルト（プロジェクト規約に合わせて調整）
        if (v == null) v = ROW_ACTIVE;
        // if (!ROW_ACTIVE.equals(v) && !ROW_STOPPED.equals(v)) {
        //     throw new IllegalArgumentException("row_status must be 'A' or 'X': " + v);
        // }
        this.row_status = v;
    }

    //  편의 메서드 / 便利メソッド 
    /** 진행중 여부 / 進行中かどうか */
    public boolean isActive() { return ROW_ACTIVE.equals(this.row_status); }

    /** 표시상태 라벨(진행중/중단) / 表示用ラベル（進行中/停止） */
    public String getRowStatusLabel() {
        return isActive() ? "進行中" : "停止";
    }

    /** 입고 대상 상태인지(발주/일부입고) / 入庫対象か（発注/一部入荷） */
    public boolean isReceivable() {
        return "発注".equals(this.order_status) || "一部入荷".equals(this.order_status);
    }

    //  내부 유틸 / 内部ユーティリティ 
    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
    private static String safe(String s) { return s == null ? null : s.trim(); }

    @Override
    public String toString() {
        return "OrderSheet{" +
                "order_id='" + order_id + '\'' +
                ", request_id='" + request_id + '\'' +
                ", supplier_id='" + supplier_id + '\'' +
                ", order_date=" + order_date +
                ", order_status='" + order_status + '\'' +
                ", row_status='" + row_status + '\'' +
                '}';
    }
}
