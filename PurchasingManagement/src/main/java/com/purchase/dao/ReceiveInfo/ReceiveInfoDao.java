package com.purchase.dao.ReceiveInfo;

import com.purchase.vo.ReceiveInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 入庫情報（receive_info）に関する永続化処理（DAO）
 * - ID発番、登録、検索（単純/結合）、ステータス更新、条件検索を担当
 *
 * 입고 정보(receive_info)에 대한 영속화 처리(DAO)
 * - ID 발급, 등록, 조회(단순/조인), 상태 갱신, 조건 검색을 담당
 */
public class ReceiveInfoDao {

    /** 
     * RI + 3桁のシーケンスを生成（例: RI001, RI002…）
     * - Oracle シーケンス receive_info_seq を使用
     *
     * RI + 3자리 시퀀스 ID 생성(예: RI001, RI002…)
     * - 오라클 시퀀스 receive_info_seq 사용
     */
    private String nextReceiveId(Connection conn) throws SQLException {
        final String sql = "SELECT receive_info_seq.NEXTVAL FROM dual";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int seq = rs.getInt(1);
                return String.format("RI%03d", seq);
            }
        }
        // 異常系：シーケンス値が取れなかった
        // 비정상: 시퀀스 값을 가져오지 못함
        throw new SQLException("receive_info_seq.NEXTVAL の取得に失敗しました / 시퀀스 값 조회 실패");
    }

    /** 
     * INSERT 処理
     * - receive_status が指定されていればそのまま利用
     * - NULL の場合は DB デフォルト（'受入済'）が適用される
     *
     * INSERT 처리
     * - receive_status 지정 시 그대로 사용
     * - NULL이면 DB 기본값('受入済') 적용
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
            // 1) キー/基本項目のバインド
            // 1) 키/기본 항목 바인딩
            ps.setString(1, receiveInfo.getReceive_id());
            ps.setString(2, receiveInfo.getOrder_id());
            ps.setString(3, receiveInfo.getProduct_id());
            ps.setInt(4, receiveInfo.getQuantity());

            // 2) 入庫日（NULLなら NVL で SYSDATE）
            // 2) 입고일(NULL이면 NVL로 SYSDATE)
            if (receiveInfo.getReceive_date() != null) {
                ps.setDate(5, new java.sql.Date(receiveInfo.getReceive_date().getTime()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            // 3) ステータス（NULLなら DB デフォルト）
            // 3) 상태(NULL이면 DB 기본값)
            ps.setString(6, receiveInfo.getReceive_status());

            ps.executeUpdate();
        }
    }

    /** 
     * 単純な全件取得（JOINなし）
     *
     * 단순 전체 조회(JOIN 없음)
     */
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

    /** 
     * receive_id から product_id を取得
     *
     * receive_id로부터 product_id 조회
     */
    public String getProductIdByReceiveId(Connection conn, String receiveId) throws SQLException {
        String productId = null;

        try (PreparedStatement pstmt = conn
                .prepareStatement("SELECT PRODUCT_ID FROM RECEIVE_INFO WHERE RECEIVE_ID = ?")) {
            pstmt.setString(1, receiveId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    productId = rs.getString("PRODUCT_ID");
                }
            }
            return productId;
        }
    }

    /**
     * JOIN を利用した一覧取得
     *  - 商品名 / 仕入先名を含む
     *  - 返品可能数量 = ri.quantity - NVL(SUM(rti.quantity), 0)
     *  - onlyAvailable=true の場合 HAVING で > 0 を絞り込み
     *  - includeHidden=false の場合、p/si/os の row_status='A' のみ対象
     *
     * JOIN을 활용한 조회
     *  - 상품명 / 공급업체명 포함
     *  - 반품 가능 수량 = ri.quantity - NVL(SUM(rti.quantity), 0)
     *  - onlyAvailable=true → HAVING > 0 필터
     *  - includeHidden=false → p/si/os의 row_status='A'만 대상
     */
    public List<ReceiveInfo> selectAllView(Connection conn,
                                           boolean onlyAvailable,
                                           boolean includeHidden) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append(
            "SELECT " +
            "  ri.receive_id, ri.order_id, ri.product_id, " +
            "  p.product_name, si.supplier_name, " +
            "  ri.quantity, ri.receive_date, ri.receive_status, " +
            "  (ri.quantity - NVL(SUM(rti.quantity), 0)) AS available_to_return " +
            "FROM receive_info ri " +
            "JOIN product       p  ON p.product_id  = ri.product_id " +
            "JOIN order_sheet   os ON os.order_id   = ri.order_id " +
            "JOIN supplier_info si ON si.supplier_id = os.supplier_id " +
            "LEFT JOIN return_info rti ON rti.receive_id = ri.receive_id "
        );
        // includeHidden=false の場合のみ row_status='A' 制約を有効化
        // includeHidden=false 인 경우에만 row_status='A' 제약을 활성화
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

        String sql = sb.toString();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // 1=include / 0=filter のスイッチ
            // 1=포함 / 0=필터 스위치
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

    /** 
     * ステータスを変更する（単一行更新）
     *
     * 상태값 변경(단일 행 업데이트)
     */
    public int updateStatus(Connection conn, String receiveId, String status) throws SQLException {
        final String sql = "UPDATE receive_info SET receive_status = ? WHERE receive_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, receiveId);
            return ps.executeUpdate();
        }
    }

    /** 
     * 返品数量を考慮した入庫情報の取得（返品可能数量 > 0 のみ）
     *
     * 반품 수량을 고려한 입고정보 조회(반품 가능 수량 > 0만)
     */
    public List<ReceiveInfo> selectReceiveInfoWithReturnQty(Connection conn) throws SQLException {
        String sql = "SELECT ri.receive_id, ri.order_id, ri.product_id, p.product_name,\r\n"
        		+ "       ri.quantity AS received_quantity,\r\n"
        		+ "       NVL(SUM(rti.quantity), 0) AS returned_quantity,\r\n"
        		+ "       ri.quantity - NVL(SUM(rti.quantity), 0) AS available_to_return,\r\n"
        		+ "       ri.receive_date\r\n"
        		+ "FROM receive_info ri\r\n"
        		+ "JOIN product p ON ri.product_id = p.product_id\r\n"
        		+ "LEFT JOIN return_info rti ON ri.receive_id = rti.receive_id\r\n"
        		+ "GROUP BY ri.receive_id, ri.order_id, ri.product_id, p.product_name, ri.quantity, ri.receive_date\r\n"
        		+ "HAVING ri.quantity - NVL(SUM(rti.quantity), 0) > 0\r\n"
        		+ "ORDER BY ri.receive_date DESC";

        List<ReceiveInfo> list = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ReceiveInfo vo = new ReceiveInfo();
                vo.setReceive_id(rs.getString("receive_id"));
                vo.setOrder_id(rs.getString("order_id"));
                vo.setProduct_id(rs.getString("product_id"));
                vo.setQuantity(rs.getInt("received_quantity"));  // 入庫数量 / 입고 수량
                vo.setReceive_date(rs.getDate("receive_date"));

                // 返品可能数量 → VO 側に専用フィールドあり
                // 반품 가능 수량 → VO 쪽 전용 필드 사용
                vo.setAvailable_to_return(rs.getInt("available_to_return"));

                list.add(vo);
            }
        }
        return list;
    }

    // 기존 static 메서드 삭제/교체
    // public static List<ReceiveInfo> findWithFilter(...){ return null; }

    /**
     * 条件付き検索（商品名/仕入先名/期間/返品可/非表示含むフラグ）
     * - LIKE は小文字化して部分一致
     * - 期間の終了日は「含める」ために toDate + 1（日付 < 翌日00:00）で実装
     *
     * 조건 검색(상품명/공급업체/기간/반품가능/숨김 포함 플래그)
     * - LIKE는 소문자 변환 후 부분 일치
     * - 종료일은 '포함'되도록 toDate + 1(다음날 0시 미만)로 처리
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
           .append("  p.product_name, si.supplier_name, ")
           .append("  ri.quantity, ri.receive_date, ri.receive_status, ")
           .append("  (ri.quantity - NVL(SUM(rti.quantity), 0)) AS available_to_return ")
           .append("FROM receive_info ri ")
           .append("JOIN product       p  ON p.product_id  = ri.product_id ")
           .append("JOIN order_sheet   os ON os.order_id   = ri.order_id ")
           .append("JOIN supplier_info si ON si.supplier_id = os.supplier_id ")
           .append("LEFT JOIN return_info rti ON rti.receive_id = ri.receive_id ")
           .append("WHERE 1=1 ");

        // 非表示を含めない場合、row_status='A' のみ
        // 숨김 포함 X이면 row_status='A'만
        if (!includeHidden) {
            sql.append("AND NVL(p.row_status,'A')='A' ")
               .append("AND NVL(si.row_status,'A')='A' ")
               .append("AND NVL(os.row_status,'A')='A' ");
        }

        List<Object> params = new ArrayList<>();

        // 商品名・仕入先名は大小無視の部分一致
        // 상품명/공급업체명은 대소문자 무시 부분일치
        if (productName != null && !productName.trim().isEmpty()) {
            sql.append("AND LOWER(p.product_name) LIKE ? ");
            params.add("%" + productName.trim().toLowerCase() + "%");
        }
        if (supplierName != null && !supplierName.trim().isEmpty()) {
            sql.append("AND LOWER(si.supplier_name) LIKE ? ");
            params.add("%" + supplierName.trim().toLowerCase() + "%");
        }
        // 期間指定（開始日/終了日+1）
        // 기간 지정(시작일/종료일+1)
        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql.append("AND ri.receive_date >= TO_DATE(?, 'YYYY-MM-DD') ");
            params.add(fromDate.trim());
        }
        if (toDate != null && !toDate.trim().isEmpty()) {
            // 終了日を含めるために +1（翌日0時未満）
            // 종료일 포함을 위해 +1(익일 0시 미만)
            sql.append("AND ri.receive_date < TO_DATE(?, 'YYYY-MM-DD') + 1 ");
            params.add(toDate.trim());
        }

        // 集計列があるため GROUP BY が必要
        // 집계 컬럼이 있으므로 GROUP BY 필요
        sql.append("GROUP BY ")
           .append("  ri.receive_id, ri.order_id, ri.product_id, ")
           .append("  p.product_name, si.supplier_name, ")
           .append("  ri.quantity, ri.receive_date, ri.receive_status ");

        // 返品可能のみ
        // 반품 가능만
        if (onlyAvailable) {
            sql.append("HAVING (ri.quantity - NVL(SUM(rti.quantity), 0)) > 0 ");
        }

        // 新しい順（入庫日 → ID）
        // 최신순(입고일 → ID)
        sql.append("ORDER BY ri.receive_date DESC, ri.receive_id DESC");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            // プレースホルダ一括バインド
            // 플레이스홀더 일괄 바인딩
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
}

