package com.purchase.dao.ReceiveInfo;

import com.purchase.vo.ReceiveInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceiveInfoDao {

    /** RI + 3桁のシーケンスを生成 (例: RI001, RI002…) */
    private String nextReceiveId(Connection conn) throws SQLException {
        final String sql = "SELECT receive_info_seq.NEXTVAL FROM dual";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int seq = rs.getInt(1);
                return String.format("RI%03d", seq);
            }
        }
        throw new SQLException("receive_info_seq.NEXTVAL の取得に失敗しました");
    }

    /** INSERT処理 (receive_status が指定されれば使用、なければDBのデフォルト '正常' を適用) */
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
            if (receiveInfo.getReceive_date() != null) {
                ps.setDate(5, new java.sql.Date(receiveInfo.getReceive_date().getTime()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setString(6, receiveInfo.getReceive_status()); // nullの場合はDEFAULT '正常' を適用
            ps.executeUpdate();
        }
    }

    /** 単純な全件取得 (JOINなし) */
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

    /** receive_id から product_id を取得 */
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

    /** ステータスを変更する */
    public int updateStatus(Connection conn, String receiveId, String status) throws SQLException {
        final String sql = "UPDATE receive_info SET receive_status = ? WHERE receive_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, receiveId);
            return ps.executeUpdate();
        }
    }

    /** 返品数量を考慮した入庫情報の取得 (返品可能数量 > 0 のみ) */
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
                vo.setQuantity(rs.getInt("received_quantity"));  // 入庫数量
                vo.setReceive_date(rs.getDate("receive_date"));

                // 返品可能数量 → ReceiveInfoに専用フィールドが必要
                vo.setAvailable_to_return(rs.getInt("available_to_return"));

                list.add(vo);
            }
        }
        return list;
    }
}

