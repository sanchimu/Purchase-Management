package com.purchase.dao.ReceiveInfo;

import com.purchase.vo.ReceiveInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 入庫情報（receive_info）DAO
 * - ID発番、登録、検索（単純/結合）、ステータス更新、条件検索
 *
 * 입고 정보(receive_info) DAO
 * - ID 발급, 등록, 조회(단순/조인), 상태 업데이트, 조건 검색
 *
 * NOTE:
 * - product は (product_id, product_name, ..., row_status) 前提
 * - order_sheet に product_id は存在しない → os.product_id 参照は禁止
 *
 * - product 테이블은 (product_id, product_name, ..., row_status) 가정
 * - order_sheet 에 product_id 없음 → os.product_id 참조 금지
 */
public class ReceiveInfoDao {

    /** 入庫ID生成 (RI + 3桁, 例: RI001) / 입고ID 생성 */
    private String nextReceiveId(Connection conn) throws SQLException {
        final String sql = "SELECT receive_info_seq.NEXTVAL FROM dual";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int seq = rs.getInt(1);
                return String.format("RI%03d", seq);
            }
        }
        throw new SQLException("receive_info_seq.NEXTVAL の取得に失敗しました / 시퀀스 값 조회 실패");
    }

    /**
     * INSERT
     * - receive_date が null → NVL で SYSDATE
     * - receive_status が null → NVL で '受入済'
     *
     * - receive_date 가 null → NVL 로 SYSDATE
     * - receive_status 가 null → NVL 로 '受入済'
     */
    public void insert(Connection conn, ReceiveInfo receiveInfo) throws SQLException {
        String newId = nextReceiveId(conn);
        receiveInfo.setReceive_id(newId);

        final String sql =
            "INSERT INTO receive_info " +
            "  (receive_id, order_id, product_id, quantity, receive_date, receive_status) " +
            "VALUES " +
            "  (?, ?, ?, ?, NVL(?, SYSDATE), NVL(?, '受入済'))";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, receiveInfo.getReceive_id());
            ps.setString(2, receiveInfo.getOrder_id());
            ps.setString(3, receiveInfo.getProduct_id());
            ps.setInt(4, receiveInfo.getQuantity());
            if (receiveInfo.getReceive_date() != null) {
                ps.setDate(5, new java.sql.Date(receiveInfo.getReceive_date().getTime()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setString(6, receiveInfo.getReceive_status());
            ps.executeUpdate();
        }
    }

    /** 単純全件取得（JOINなし） / 단순 전체 조회(JOIN 없음) */
    public List<ReceiveInfo> selectAll(Connection conn) throws SQLException {
        final String sql =
            "SELECT receive_id, order_id, product_id, quantity, receive_date, receive_status " +
            "FROM receive_info ORDER BY receive_id";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<ReceiveInfo> list = new ArrayList<>();
            while (rs.next()) {
                ReceiveInfo ri = new ReceiveInfo();
                ri.setReceive_id(rs.getString("receive_id"));
                ri.setOrder_id(rs.getString("order_id"));
                ri.setProduct_id(rs.getString("product_id"));
                ri.setQuantity(rs.getInt("quantity"));
                ri.setReceive_date(rs.getDate("receive_date"));
                ri.setReceive_status(rs.getString("receive_status"));
                list.add(ri);
            }
            return list;
        }
    }

    /** receive_id → product_id 取得 / 조회 */
    public String getProductIdByReceiveId(Connection conn, String receiveId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT PRODUCT_ID FROM RECEIVE_INFO WHERE RECEIVE_ID = ?")) {
            pstmt.setString(1, receiveId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getString("PRODUCT_ID") : null;
            }
        }
    }

    /**
     * JOIN一覧（商品名/仕入先/返品可能数量を含む）
     * - product は ri.product_id で LEFT JOIN（商品が無くても行は残る）
     * - supplier_info は order_sheet 経由
     * - return_info は LEFT JOIN 集計で差し引き
     *
     * JOIN 조회(상품명/공급업체/반품가능 포함)
     * - product 는 ri.product_id 로 LEFT JOIN(상품 없어도 행 유지)
     * - supplier_info 는 order_sheet 경유
     * - return_info 는 LEFT JOIN 집계로 차감
     */
    public List<ReceiveInfo> selectAllView(Connection conn,
                                           boolean onlyAvailable,
                                           boolean includeHidden) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append(
            "SELECT " +
            "  ri.receive_id, ri.order_id, ri.product_id, " +
            "  p.product_name AS product_name, si.supplier_name, " +
            "  ri.quantity, ri.receive_date, ri.receive_status, " +
            "  (ri.quantity - NVL(SUM(rti.quantity), 0)) AS available_to_return " +
            "FROM receive_info ri " +
            "JOIN order_sheet   os ON os.order_id    = ri.order_id " +
            "LEFT JOIN product  p  ON p.product_id   = ri.product_id " +
            "JOIN supplier_info si ON si.supplier_id = os.supplier_id " +
            "LEFT JOIN return_info rti ON rti.receive_id = ri.receive_id "
        );
        // includeHidden=false の場合のみ row_status='A' 条件
        // includeHidden=false 인 경우에만 row_status='A' 필터
        sb.append("WHERE (? = 1 OR (NVL(p.row_status,'A')='A' AND NVL(si.row_status,'A')='A' AND NVL(os.row_status,'A')='A')) ");
        sb.append(
            "GROUP BY " +
            "  ri.receive_id, ri.order_id, ri.product_id, " +
            "  p.product_name, si.supplier_name, " +
            "  ri.quantity, ri.receive_date, ri.receive_status "
        );
        if (onlyAvailable) {
            sb.append("HAVING (ri.quantity - NVL(SUM(rti.quantity), 0)) > 0 ");
        }
        sb.append("ORDER BY ri.receive_id");

        try (PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            ps.setInt(1, includeHidden ? 1 : 0);
            try (ResultSet rs = ps.executeQuery()) {
                List<ReceiveInfo> list = new ArrayList<>();
                while (rs.next()) {
                    ReceiveInfo vo = new ReceiveInfo();
                    vo.setReceive_id(rs.getString("receive_id"));
                    vo.setOrder_id(rs.getString("order_id"));
                    vo.setProduct_id(rs.getString("product_id"));
                    vo.setProduct_name(rs.getString("product_name"));
                    vo.setSupplier_name(rs.getString("supplier_name"));
                    vo.setQuantity(rs.getInt("quantity"));
                    vo.setReceive_date(rs.getDate("receive_date"));
                    vo.setReceive_status(rs.getString("receive_status"));
                    vo.setAvailable_to_return(rs.getInt("available_to_return"));
                    list.add(vo);
                }
                return list;
            }
        }
    }

    /** ステータス変更（単一行） / 상태값 변경(단일 행) */
    public int updateStatus(Connection conn, String receiveId, String status) throws SQLException {
        final String sql = "UPDATE receive_info SET receive_status = ? WHERE receive_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, receiveId);
            return ps.executeUpdate();
        }
    }

    /**
     * 返品考慮入庫（available_to_return > 0 のみ、商品名付き）
     * - product を LEFT JOIN して product_name を AS で明示
     * - return_info は LEFT JOIN 集計で差し引き
     *
     * 반품 가능 입고만(>0), 상품명 포함
     * - product LEFT JOIN, product_name 별칭
     */
    public List<ReceiveInfo> selectReceiveInfoWithReturnQty(Connection conn) throws SQLException {
        String sql =
            "SELECT " +
            "  ri.receive_id, " +
            "  ri.order_id, " +
            "  ri.product_id, " +
            "  p.product_name AS product_name, " +         // 상품명 별칭(중요)
            "  ri.quantity AS received_quantity, " +       // 입고 수량
            "  ri.receive_date, " +
            "  NVL(SUM(rti.quantity), 0) AS returned_quantity, " + // 반품 합계
            "  (ri.quantity - NVL(SUM(rti.quantity), 0)) AS available_to_return " + // 반품 가능 수량
            "FROM receive_info ri " +
            "LEFT JOIN return_info rti ON ri.receive_id = rti.receive_id " + // 반품 집계
            "JOIN order_sheet   os ON os.order_id    = ri.order_id " +       // 발주 정보(확장 대비)
            "LEFT JOIN product  p  ON p.product_id   = ri.product_id " +     // 상품 연결(LEFT로 안전)
            "GROUP BY " +
            "  ri.receive_id, ri.order_id, ri.product_id, " +
            "  p.product_name, " +
            "  ri.quantity, ri.receive_date " +
            "HAVING (ri.quantity - NVL(SUM(rti.quantity), 0)) > 0 " +        // 반품 가능 > 0
            "ORDER BY ri.receive_date DESC";

        List<ReceiveInfo> list = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ReceiveInfo vo = new ReceiveInfo();
                vo.setReceive_id(rs.getString("receive_id"));
                vo.setOrder_id(rs.getString("order_id"));
                vo.setProduct_id(rs.getString("product_id"));
                vo.setProduct_name(rs.getString("product_name")); // 별칭으로 안전 추출
                vo.setQuantity(rs.getInt("received_quantity"));   // 입고 수량
                vo.setReceive_date(rs.getDate("receive_date"));
                vo.setAvailable_to_return(rs.getInt("available_to_return")); // 반품 가능 수량
                list.add(vo);
            }
        }
        return list;
    }

    /**
     * 条件検索（商品名/仕入先/期間/返品可/非表示含む）
     * - product.product_name を AS product_name で返す
     * - includeHidden=false の場合 row_status='A'
     * - LIKE は小文字化
     *
     * 조건 검색(상품명/공급업체/기간/반품가능/숨김 포함)
     * - product.product_name 을 AS product_name 으로 노출
     * - includeHidden=false 시 row_status='A' 필터
     * - LIKE 는 소문자 변환
     */
    public List<ReceiveInfo> findWithFilter(
            Connection conn,
            String productName,
            String supplierName,
            String fromDate,   // "YYYY-MM-DD"
            String toDate,     // "YYYY-MM-DD"
            boolean onlyAvailable,
            boolean includeHidden
    ) throws SQLException {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("  ri.receive_id, ri.order_id, ri.product_id, ")
           .append("  p.product_name AS product_name, si.supplier_name, ")
           .append("  ri.quantity, ri.receive_date, ri.receive_status, ")
           .append("  (ri.quantity - NVL(SUM(rti.quantity), 0)) AS available_to_return ")
           .append("FROM receive_info ri ")
           .append("JOIN order_sheet   os ON os.order_id    = ri.order_id ")
           .append("LEFT JOIN product  p  ON p.product_id   = ri.product_id ")
           .append("JOIN supplier_info si ON si.supplier_id = os.supplier_id ")
           .append("LEFT JOIN return_info rti ON rti.receive_id = ri.receive_id ")
           .append("WHERE 1=1 ");

        if (!includeHidden) {
            sql.append("AND NVL(p.row_status,'A')='A' ")
               .append("AND NVL(si.row_status,'A')='A' ")
               .append("AND NVL(os.row_status,'A')='A' ");
        }

        List<Object> params = new ArrayList<>();

        if (productName != null && !productName.trim().isEmpty()) {
            sql.append("AND LOWER(p.product_name) LIKE ? ");
            params.add("%" + productName.trim().toLowerCase() + "%");
        }
        if (supplierName != null && !supplierName.trim().isEmpty()) {
            sql.append("AND LOWER(si.supplier_name) LIKE ? ");
            params.add("%" + supplierName.trim().toLowerCase() + "%");
        }
        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql.append("AND ri.receive_date >= TO_DATE(?, 'YYYY-MM-DD') ");
            params.add(fromDate.trim());
        }
        if (toDate != null && !toDate.trim().isEmpty()) {
            sql.append("AND ri.receive_date < TO_DATE(?, 'YYYY-MM-DD') + 1 ");
            params.add(toDate.trim());
        }

        sql.append("GROUP BY ")
           .append("  ri.receive_id, ri.order_id, ri.product_id, ")
           .append("  p.product_name, si.supplier_name, ")
           .append("  ri.quantity, ri.receive_date, ri.receive_status ");

        if (onlyAvailable) {
            sql.append("HAVING (ri.quantity - NVL(SUM(rti.quantity), 0)) > 0 ");
        }

        sql.append("ORDER BY ri.receive_date DESC, ri.receive_id DESC");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<ReceiveInfo> list = new ArrayList<>();
                while (rs.next()) {
                    ReceiveInfo vo = new ReceiveInfo();
                    vo.setReceive_id(rs.getString("receive_id"));
                    vo.setOrder_id(rs.getString("order_id"));
                    vo.setProduct_id(rs.getString("product_id"));
                    vo.setProduct_name(rs.getString("product_name"));
                    vo.setSupplier_name(rs.getString("supplier_name"));
                    vo.setQuantity(rs.getInt("quantity"));
                    vo.setReceive_date(rs.getDate("receive_date"));
                    vo.setReceive_status(rs.getString("receive_status"));
                    vo.setAvailable_to_return(rs.getInt("available_to_return"));
                    list.add(vo);
                }
                return list;
            }
        }
    }

    // =====================================================================
    // SQLは変えずに Java 側で product_name を補うヘルパ / SQL 안 바꾸고 자바로 보강
    // =====================================================================

    /** order_id → request_id 取得 / 조회 */
    public String getRequestIdByOrderId(Connection conn, String orderId) throws SQLException {
        final String sql = "SELECT request_id FROM order_sheet WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    /** request_id → product_id 取得 / 조회 */
    public String getProductIdByRequestId(Connection conn, String requestId) throws SQLException {
        final String sql = "SELECT product_id FROM purchase_request WHERE request_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    /** product_id → product_name 取得 / 조회 */
    public String getProductNameByProductId(Connection conn, String productId) throws SQLException {
        final String sql = "SELECT product_name FROM product WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    /**
     * 取得済みリストに product_name を埋める（Java 後処理）
     * - product_id が空なら order_sheet → purchase_request で補完
     * - 同じ product_id はキャッシュ
     *
     * 조회된 리스트에 product_name 보강
     * - product_id 비면 order_sheet → purchase_request 로 보완
     * - 동일 product_id 는 캐시
     */
    public void enrichProductNames(Connection conn, List<ReceiveInfo> list) throws SQLException {
        if (list == null || list.isEmpty()) return;

        java.util.Map<String, String> nameCache = new java.util.HashMap<>();

        for (ReceiveInfo vo : list) {
            if (vo.getProduct_name() != null && !vo.getProduct_name().isEmpty()) continue;

            String pid = vo.getProduct_id();

            if (pid == null || pid.isEmpty()) {
                String reqId = getRequestIdByOrderId(conn, vo.getOrder_id());
                if (reqId != null) {
                    pid = getProductIdByRequestId(conn, reqId);
                    if (pid != null) {
                        vo.setProduct_id(pid); // 보완된 product_id 를 VO에 반영(선택)
                    }
                }
            }

            if (pid != null && !pid.isEmpty()) {
                String name = nameCache.get(pid);
                if (name == null) {
                    name = getProductNameByProductId(conn, pid);
                    if (name != null) nameCache.put(pid, name);
                }
                vo.setProduct_name(name); // null 일 수도 있음(그대로 둠)
            }
        }
    }
}

