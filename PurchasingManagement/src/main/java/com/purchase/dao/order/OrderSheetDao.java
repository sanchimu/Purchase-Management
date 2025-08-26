package com.purchase.dao.order;

import com.purchase.dto.OrderSheetDTO;
import com.purchase.vo.OrderSheet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 발주서 DAO / 発注書DAO
 *   DB의 일본어 상태값과 정확히 일치하도록 값 세팅/비교 필요
 *   （DBの日本語状態値と完全一致させること）
 *
 * 상태 세트 / 状態セット:
 *   order_status: 「発注依頼」「承認」「発注」「一部入荷」「完了」「取消」「保留」
 *   row_status  : 'A'(進行中) / 'X'(停止)
 */
public class OrderSheetDao {

    // region 목록(조인) 조회 / 一覧（JOIN）取得
    /**
     * 목록(조인) 조회 / 一覧（JOIN）取得
     * includeHidden=false → row_status='A'만 노출
     * includeHidden=true  → A/X 전체 노출
     */
    public List<OrderSheetDTO> selectAllView(Connection conn, boolean includeHidden) throws SQLException {
        String sql =
            "SELECT " +
            "  os.order_id, os.order_date, os.order_status, os.row_status, " +
            "  os.request_id, os.supplier_id, " +
            "  pr.product_id, pr.quantity AS request_quantity, pr.requester_name, " +
            "  p.product_name, " +
            "  si.supplier_name " +
            "FROM order_sheet os " +
            "JOIN purchase_request pr ON pr.request_id = os.request_id " +
            "JOIN product p           ON p.product_id  = pr.product_id " +
            "JOIN supplier_info si    ON si.supplier_id = os.supplier_id " +
            "WHERE (? = 1 OR NVL(os.row_status,'A') = 'A') " +   // includeHidden=0이면 A만 / includeHidden=0ならAのみ
            "ORDER BY os.order_id";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, includeHidden ? 1 : 0);
            try (ResultSet rs = ps.executeQuery()) {
                List<OrderSheetDTO> list = new ArrayList<>();
                while (rs.next()) {
                    OrderSheetDTO dto = new OrderSheetDTO();
                    dto.setOrder_id(rs.getString("order_id"));
                    dto.setOrder_date(rs.getDate("order_date"));
                    dto.setOrder_status(rs.getString("order_status"));
                    dto.setRow_status(rs.getString("row_status"));

                    dto.setRequest_id(rs.getString("request_id"));
                    dto.setSupplier_id(rs.getString("supplier_id"));

                    dto.setProduct_id(rs.getString("product_id"));
                    dto.setRequest_quantity(rs.getInt("request_quantity"));
                    dto.setRequester_name(rs.getString("requester_name"));

                    dto.setProduct_name(rs.getString("product_name"));
                    dto.setSupplier_name(rs.getString("supplier_name"));

                    list.add(dto);
                }
                return list;
            }
        }
    }
    
    // region 발주 등록용 드롭다운(구매요청) / 発注登録用ドロップダウン（購買申請）
    /**
     * 발주 등록 시 선택용 데이터 / 発注登録時の選択データ
     *   표시상태 A 인 요청/상품/공급업체만 / 表示状態Aのみ
     *   필요시 구매요청 상태 제약(例：受付/審査中/承認)을 여기에 추가 가능
     */
    public List<OrderSheetDTO> selectRequestOptions(Connection conn) throws SQLException {
        String sql =
            "SELECT pr.request_id, pr.product_id, pr.quantity AS request_quantity, pr.requester_name, " +
            "       p.product_name, p.supplier_id, si.supplier_name " +
            "FROM purchase_request pr " +
            "JOIN product p        ON p.product_id = pr.product_id " +
            "JOIN supplier_info si ON si.supplier_id = p.supplier_id " +
            "WHERE NVL(pr.row_status,'A')='A' " +
            "  AND NVL(p.row_status,'A')='A' " +
            "  AND NVL(si.row_status,'A')='A' " +
            // 구매요청 상태 필터 예시(필요 시 해제/수정) / 購買申請状態の例（必要に応じて）
            // "  AND pr.request_status IN ('受付','審査中','承認') " +
            "ORDER BY pr.request_id";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<OrderSheetDTO> list = new ArrayList<>();
            while (rs.next()) {
                OrderSheetDTO dto = new OrderSheetDTO();
                dto.setRequest_id(rs.getString("request_id"));
                dto.setProduct_id(rs.getString("product_id"));
                dto.setRequest_quantity(rs.getInt("request_quantity"));
                dto.setRequester_name(rs.getString("requester_name"));

                dto.setProduct_name(rs.getString("product_name"));
                dto.setSupplier_id(rs.getString("supplier_id"));
                dto.setSupplier_name(rs.getString("supplier_name"));
                list.add(dto);
            }
            return list;
        }
    }

    // region 유틸: request_id → supplier_id / ユーティリティ: request_id → supplier_id
    /**
     * request_id로 supplier_id 획득 / request_id から supplier_id を取得
     *   서버에서 공급업체 자동 결정을 위해 사용
     */
    public String findSupplierIdByRequestId(Connection conn, String requestId) throws SQLException {
        String sql =
            "SELECT p.supplier_id " +
            "FROM purchase_request pr " +
            "JOIN product p ON p.product_id = pr.product_id " +
            "WHERE pr.request_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trimOrNull(requestId));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    // region 다음 발주ID 생성 / 次の発注ID生成
    /**
     * 다음 발주ID / 次の発注ID
     *   시퀀스값을 받아 접두사 OR + 제로패딩 / シーケンス値に接頭辞OR＋ゼロパディング
     *   999 넘어가면 자리수 자동 확장 / 999超過時は桁数自動拡張
     */
    public String nextOrderId(Connection conn) throws SQLException {
        String sql = "SELECT order_sheet_seq.NEXTVAL FROM dual";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long seq = rs.getLong(1);
                // 3자리 기본, 4자리 이상도 자동 대응 / 基本3桁、4桁以上も自動対応
                int width = (seq < 1000) ? 3 : (seq < 10000 ? 4 : 6);
                return "OR" + String.format("%0" + width + "d", seq);
            }
        }
        throw new SQLException("order_sheet_seq.NEXTVAL 조회 실패 / 取得失敗");
    }

    // region INSERT / 発注登録
    /**
     * INSERT(발주 등록) / INSERT（発注登録）
     *   null/공백 방어: 상태값 트림, row_status 기본 A
     *   null/空白ガード：状態値trim、row_statusはデフォルトA
     */
    public void insert(Connection conn, OrderSheet order) throws SQLException {
        String sql = "INSERT INTO order_sheet (order_id, request_id, supplier_id, order_date, order_status, row_status) " +
                     "VALUES (?, ?, ?, ?, ?, NVL(?, 'A'))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // 필수값 방어 / 必須値ガード
            if (order.getOrder_date() == null) {
                // DB DEFAULT SYSDATE를 쓰고 싶으면 null로 넣는 대신 SQL에서 SYSDATE를 사용하도록 변경
                // DBのDEFAULT SYSDATEを使うなら、SQL側でSYSDATE使用に変更
                order.setOrder_date(new java.util.Date());
            }
            ps.setString(1, safe(order.getOrder_id()));
            ps.setString(2, safe(order.getRequest_id()));
            ps.setString(3, safe(order.getSupplier_id()));
            ps.setDate(4, new java.sql.Date(order.getOrder_date().getTime()));
            ps.setString(5, safe(order.getOrder_status()));   // 예: 「発注依頼」
            ps.setString(6, safe(order.getRow_status()));     // null → NVL로 'A'
            ps.executeUpdate();
        }
    }

    // region 상태 변경 / 業務状態変更
    /**
     * 업무상태 변경 / 業務状態の変更
     *   값은 DB CHECK와 일치해야 함 / 値はDBのCHECKと一致させる
     */
    public void updateStatus(Connection conn, OrderSheet order) throws SQLException {
        String sql = "UPDATE order_sheet SET order_status=? WHERE order_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, safe(order.getOrder_status()));
            ps.setString(2, safe(order.getOrder_id()));
            ps.executeUpdate();
        }
    }

    // region 표시상태(A/X) 변경 / 表示状態(A/X)変更
    /**
     * 표시상태(A/X) 변경 / 表示状態(A/X)の変更
     */
    public void updateRowStatus(Connection conn, String orderId, String rowStatus) throws SQLException {
        String sql = "UPDATE order_sheet SET row_status=? WHERE order_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, safe(rowStatus)); // 'A' or 'X'
            ps.setString(2, safe(orderId));
            ps.executeUpdate();
        }
    }
    // 

    // region 내부 유틸 / 内部ユーティリティ
    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
    private static String safe(String s) {
        return s == null ? null : s.trim();
    }
    
 // region 검색(조인) 조건 / 検索（JOIN）条件
    /**
     * 조건 검색(조인뷰) / 条件検索（JOINビュー）
     * params:
     *   order_id, request_id, supplier_id → LIKE
     *   order_status → =
     *   from_date, to_date → order_date 範囲 (yyyy-MM-dd)
     *   includeHidden: "1"이면 A/X 모두, 아니면 A만
     */
    public List<OrderSheetDTO> selectViewByConditions(Connection conn, Map<String,String> params) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("  os.order_id, os.order_date, os.order_status, os.row_status, ")
           .append("  os.request_id, os.supplier_id, ")
           .append("  pr.product_id, pr.quantity AS request_quantity, pr.requester_name, ")
           .append("  p.product_name, ")
           .append("  si.supplier_name ")
           .append("FROM order_sheet os ")
           .append("JOIN purchase_request pr ON pr.request_id = os.request_id ")
           .append("JOIN product p           ON p.product_id  = pr.product_id ")
           .append("JOIN supplier_info si    ON si.supplier_id = os.supplier_id ")
           .append("WHERE 1=1 ");

        List<Object> bind = new ArrayList<>();

        String orderId    = trimOrNull(params.get("order_id"));
        String requestId  = trimOrNull(params.get("request_id"));
        String supplierId = trimOrNull(params.get("supplier_id"));
        String status     = trimOrNull(params.get("order_status"));
        String fromDate   = trimOrNull(params.get("from_date"));
        String toDate     = trimOrNull(params.get("to_date"));
        boolean includeHidden = "1".equals(trimOrNull(params.get("includeHidden")));

        if (!includeHidden) {
            sql.append("AND NVL(os.row_status,'A')='A' ");
        }
        if (orderId != null) {
            sql.append("AND os.order_id LIKE ? ");
            bind.add("%" + orderId + "%");
        }
        if (requestId != null) {
            sql.append("AND os.request_id LIKE ? ");
            bind.add("%" + requestId + "%");
        }
        if (supplierId != null) {
            sql.append("AND os.supplier_id LIKE ? ");
            bind.add("%" + supplierId + "%");
        }
        if (status != null) {
            sql.append("AND os.order_status = ? ");
            bind.add(status);
        }
        if (fromDate != null) {
            sql.append("AND os.order_date >= ? ");
            bind.add(Date.valueOf(fromDate)); // yyyy-MM-dd
        }
        if (toDate != null) {
            sql.append("AND os.order_date < ? + 1 ");
            bind.add(Date.valueOf(toDate));   // 종료일 포함
        }

        sql.append("ORDER BY os.order_id");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object o : bind) ps.setObject(idx++, o);
            try (ResultSet rs = ps.executeQuery()) {
                List<OrderSheetDTO> list = new ArrayList<>();
                while (rs.next()) {
                    OrderSheetDTO dto = new OrderSheetDTO();
                    dto.setOrder_id(rs.getString("order_id"));
                    dto.setOrder_date(rs.getDate("order_date"));
                    dto.setOrder_status(rs.getString("order_status"));
                    dto.setRow_status(rs.getString("row_status"));

                    dto.setRequest_id(rs.getString("request_id"));
                    dto.setSupplier_id(rs.getString("supplier_id"));

                    dto.setProduct_id(rs.getString("product_id"));
                    dto.setRequest_quantity(rs.getInt("request_quantity"));
                    dto.setRequester_name(rs.getString("requester_name"));

                    dto.setProduct_name(rs.getString("product_name"));
                    dto.setSupplier_name(rs.getString("supplier_name"));

                    list.add(dto);
                }
                return list;
            }
        }
    }
    

}
