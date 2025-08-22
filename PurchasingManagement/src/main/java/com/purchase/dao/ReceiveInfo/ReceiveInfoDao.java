package com.purchase.dao.ReceiveInfo;

import com.purchase.vo.ReceiveInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceiveInfoDao {

    /** RI + 3桁のシーケンスを生成 (例: RI001, RI002…) 
     *  RI + 3자리 시퀀스 ID 생성 (예: RI001, RI002…) */
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

    /** INSERT処理 
     *  receive_status が指定されれば使用、なければDBのデフォルト '正常' を適用 
     *  INSERT 처리 (상태가 지정되면 적용, 없으면 DB 기본값 '정상' 적용) */
    public void insert(Connection conn, ReceiveInfo receiveInfo) throws SQLException {
        String newId = nextReceiveId(conn);
        receiveInfo.setReceive_id(newId);

        final String sql =
            "INSERT INTO receive_info " +
            "  (receive_id, order_id, product_id, quantity, receive_date, receive_status) " +
            "VALUES " +
            "  (?, ?, ?, ?, NVL(?, SYSDATE), NVL(?, '正常'))";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, receiveInfo.getReceive_id());
            ps.setString(2, receiveInfo.getOrder_id());
            ps.setString(3, receiveInfo.getProduct_id());
            ps.setInt(4, receiveInfo.getQuantity());

            // 入庫日が存在すればセット、なければNULL
            // 입고일이 있으면 세팅, 없으면 NULL
            if (receiveInfo.getReceive_date() != null) {
                ps.setDate(5, new java.sql.Date(receiveInfo.getReceive_date().getTime()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setString(6, receiveInfo.getReceive_status()); 
            // null の場合は DEFAULT '正常' が適用される
            // null이면 DB 기본값 '정상' 적용
            ps.executeUpdate();
        }
    }

    /** 単純な全件取得 (JOINなし) 
     *  단순 전체 조회 (JOIN 없음) */
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

    /** receive_id から product_id を取得 
     *  receive_id로부터 product_id 조회 */
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
     * JOINを利用した一括取得:
     *  - 商品名 / 仕入先名 を含む
     *  - 返品可能数量 = ri.quantity - NVL(SUM(rti.quantity),0)
     *  - onlyAvailable=true の場合 HAVING > 0
     *  - includeHidden=false の場合 p/si/os の row_status='A' のみ
     *
     * JOIN을 활용한 조회:
     *  - 상품명 / 공급업체명 포함
     *  - 반품 가능 수량 = ri.quantity - NVL(SUM(rti.quantity),0)
     *  - onlyAvailable=true → HAVING 조건으로 > 0
     *  - includeHidden=false → p/si/os의 row_status='A'만 조회
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
        // 非表示データを含めない場合、row_status='A' のみ抽出
        // 숨김 데이터를 포함하지 않으면 row_status='A'만 추출
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
            ps.setInt(1, includeHidden ? 1 : 0); // WHERE (?=1 OR …)
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

    /** ステータスを変更する 
     *  상태값 변경 */
    public int updateStatus(Connection conn, String receiveId, String status) throws SQLException {
        final String sql = "UPDATE receive_info SET receive_status = ? WHERE receive_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, receiveId);
            return ps.executeUpdate();
        }
    }

    /** 返品数量を考慮した入庫情報の取得 (返品可能数量 > 0 のみ)
     *  반품 수량을 고려한 입고정보 조회 (반품 가능 수량 > 0 만) */
    public List<ReceiveInfo> selectReceiveInfoWithReturnQty(Connection conn) throws SQLException {
        String sql = "SELECT ri.receive_id, ri.order_id, ri.product_id, ri.quantity AS received_quantity, ri.receive_date, "
                   + "       NVL(SUM(rti.quantity), 0) AS returned_quantity, "
                   + "       ri.quantity - NVL(SUM(rti.quantity), 0) AS available_to_return "
                   + "FROM receive_info ri "
                   + "LEFT JOIN return_info rti ON ri.receive_id = rti.receive_id "
                   + "GROUP BY ri.receive_id, ri.order_id, ri.product_id, ri.quantity, ri.receive_date "
                   + "HAVING ri.quantity - NVL(SUM(rti.quantity), 0) > 0 "
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

                // 返品可能数量 → ReceiveInfoに専用フィールドが必要
                // 반품 가능 수량 → ReceiveInfo VO에 필드 필요
                vo.setAvailable_to_return(rs.getInt("available_to_return"));

                list.add(vo);
            }
        }
        return list;
    }
}
