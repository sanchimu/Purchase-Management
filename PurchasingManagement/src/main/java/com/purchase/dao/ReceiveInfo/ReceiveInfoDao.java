package com.purchase.dao.ReceiveInfo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.purchase.vo.ReceiveInfo;

import jdbc.JdbcUtil;

public class ReceiveInfoDao {
    public ReceiveInfo insert(Connection conn, ReceiveInfo recInfo) throws SQLException {
        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            int seqNum = 0;
            pstmt = conn.prepareStatement("select receive_seq.nextval from dual");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                seqNum = rs.getInt(1);
            }
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);

            // receive_id 만들기: "R" + 3자리 숫자 (ex. R001)
            String receiveId = String.format("RI%03d", seqNum);
            recInfo.setReceive_id(receiveId);

            pstmt = conn.prepareStatement("insert into receive_info values (?,?,?,?,?)");
            pstmt.setString(1, recInfo.getReceive_id());
            pstmt.setString(2, recInfo.getOrder_id());
            pstmt.setString(3, recInfo.getProduct_id());
            pstmt.setInt(4, recInfo.getQuantity());
            pstmt.setDate(5, new Date(recInfo.getReceive_date().getTime()));
            int insertedCount = pstmt.executeUpdate();

            if (insertedCount > 0) {
                stmt = conn.createStatement();
                rs = stmt.executeQuery(
                        "select * from (select receive_id from receive_info order by receive_id desc) where rownum = 1");
                if (rs.next()) {
                    return new ReceiveInfo(
                        recInfo.getReceive_id(),
                        recInfo.getOrder_id(),
                        recInfo.getProduct_id(),
                        recInfo.getQuantity(),
                        recInfo.getReceive_date()
                    );
                }
            }
            return null;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
            JdbcUtil.close(stmt);
        }
    }

    public int delete(Connection conn, String receiveId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("delete from receive_info where receive_id = ?")) {
            pstmt.setString(1, receiveId);
            return pstmt.executeUpdate();
        }
    }

    public List<ReceiveInfo> selectAll(Connection conn) throws SQLException {
        String sql = "SELECT * FROM receive_info ORDER BY receive_id";
        List<ReceiveInfo> list = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ReceiveInfo r = new ReceiveInfo(
                    rs.getString("receive_id"),
                    rs.getString("order_id"),
                    rs.getString("product_id"),
                    rs.getInt("quantity"),
                    rs.getDate("receive_date")
                );
                list.add(r);
            }
        }
        return list;
    }

    public List<ReceiveInfo> selectByConditions(Connection conn, Map<String, String> conditions) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM receive_info WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (conditions.get("receive_id") != null) {
            sql.append(" AND receive_id = ?");
            params.add(conditions.get("receive_id"));
        }
        if (conditions.get("order_id") != null) {
            sql.append(" AND order_id = ?");
            params.add(conditions.get("order_id"));
        }
        if (conditions.get("product_id") != null) {
            sql.append(" AND product_id = ?");
            params.add(conditions.get("product_id"));
        }
        if (conditions.get("quantity") != null) {
            sql.append(" AND quantity = ?");
            params.add(Integer.parseInt(conditions.get("quantity")));
        }
        if (conditions.get("receive_date") != null) {
            sql.append(" AND receive_date = TO_DATE(?, 'YYYY-MM-DD')");
            params.add(conditions.get("receive_date"));
        }
        sql.append(" ORDER BY receive_id");

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                List<ReceiveInfo> list = new ArrayList<>();
                while (rs.next()) {
                    ReceiveInfo r = new ReceiveInfo(
                        rs.getString("receive_id"),
                        rs.getString("order_id"),
                        rs.getString("product_id"),
                        rs.getInt("quantity"),
                        rs.getDate("receive_date")
                    );
                    list.add(r);
                }
                return list;
            }
        }
    }
}