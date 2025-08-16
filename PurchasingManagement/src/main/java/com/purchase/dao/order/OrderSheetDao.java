package com.purchase.dao.order;

import com.purchase.dto.OrderSheetDTO;
import com.purchase.vo.OrderSheet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderSheetDao {

    /** 목록(조인) 조회: includeHidden=false면 row_status='A'만 */
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
            "WHERE (? = 1 OR NVL(os.row_status,'A') = 'A') " +
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

    /** 드롭다운용: 진행중(A) 요청만 + 상품명/공급업체명까지 */
    public List<OrderSheetDTO> selectRequestOptions(Connection conn) throws SQLException {
        String sql =
            "SELECT pr.request_id, pr.product_id, pr.quantity AS request_quantity, pr.requester_name, " +
            "       p.product_name, p.supplier_id, si.supplier_name " +
            "FROM purchase_request pr " +
            "JOIN product p        ON p.product_id = pr.product_id " +
            "JOIN supplier_info si ON si.supplier_id = p.supplier_id " +
            "WHERE NVL(pr.row_status,'A')='A' AND NVL(p.row_status,'A')='A' AND NVL(si.row_status,'A')='A' " +
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

    /** request_id → supplier_id (서버 검증/자동결정용) */
    public String findSupplierIdByRequestId(Connection conn, String requestId) throws SQLException {
        String sql =
            "SELECT p.supplier_id " +
            "FROM purchase_request pr " +
            "JOIN product p ON p.product_id = pr.product_id " +
            "WHERE pr.request_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    /** 다음 발주ID: OR + 3자리 (시퀀스) */
    public String nextOrderId(Connection conn) throws SQLException {
        String sql = "SELECT order_sheet_seq.NEXTVAL FROM dual";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long seq = rs.getLong(1);
                return String.format("OR%03d", seq);
            }
        }
        throw new SQLException("order_sheet_seq.NEXTVAL 조회 실패");
    }

    /** INSERT(발주 등록) */
    public void insert(Connection conn, OrderSheet order) throws SQLException {
        String sql = "INSERT INTO order_sheet (order_id, request_id, supplier_id, order_date, order_status, row_status) " +
                     "VALUES (?, ?, ?, ?, ?, NVL(?, 'A'))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, order.getOrder_id());
            ps.setString(2, order.getRequest_id());
            ps.setString(3, order.getSupplier_id());
            ps.setDate(4, new java.sql.Date(order.getOrder_date().getTime()));
            ps.setString(5, order.getOrder_status());
            ps.setString(6, order.getRow_status()); // null이면 A
            ps.executeUpdate();
        }
    }

    /** 업무상태 변경 */
    public void updateStatus(Connection conn, OrderSheet order) throws SQLException {
        String sql = "UPDATE order_sheet SET order_status=? WHERE order_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, order.getOrder_status());
            ps.setString(2, order.getOrder_id());
            ps.executeUpdate();
        }
    }

    /** 표시상태(A/X) 변경 */
    public void updateRowStatus(Connection conn, String orderId, String rowStatus) throws SQLException {
        String sql = "UPDATE order_sheet SET row_status=? WHERE order_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rowStatus);
            ps.setString(2, orderId);
            ps.executeUpdate();
        }
    }
}
