package com.purchase.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jdbc.connection.ConnectionProvider;   // ← 네 프로젝트 패키지에 맞춤
import mvc.command.CommandHandler;

public class StatusBulkHandler implements CommandHandler {

    // 허용 테이블/PK 화이트리스트 (SQL 인젝션 방지)
    private static final Map<String, String> ALLOWED = new HashMap<>();
    static {
        ALLOWED.put("supplier_info",    "supplier_id");
        ALLOWED.put("product",          "product_id");
        ALLOWED.put("purchase_request", "request_id");
        ALLOWED.put("order_sheet",      "order_id");
    }

    @Override
    public String process(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String to = req.getParameter("to");                 // "A" or "X"
        String table = req.getParameter("table");
        String idCol = req.getParameter("idColumn");
        String[] ids = req.getParameterValues("ids");

        // 소문자 정규화(폼에서 대문자로 와도 안전)
        if (table != null) table = table.toLowerCase(Locale.ROOT).trim();
        if (idCol != null) idCol = idCol.toLowerCase(Locale.ROOT).trim();

        // 기본 검증
        if (!"A".equals(to) && !"X".equals(to)) {
            redirectBack(req, resp);
            return null;
        }
        if (table == null || idCol == null || ids == null || ids.length == 0) {
            redirectBack(req, resp);
            return null;
        }

        // 화이트리스트 체크
        String allowedPk = ALLOWED.get(table);
        if (allowedPk == null || !allowedPk.equals(idCol)) {
            redirectBack(req, resp);
            return null;
        }

        // ? 자리수 만들기
        String placeholders = String.join(",", Collections.nCopies(ids.length, "?"));
        String sql = "UPDATE " + table + " SET row_status=? WHERE " + idCol + " IN (" + placeholders + ")";

        try (Connection con = ConnectionProvider.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, to);  // row_status
            int idx = 2;
            for (String id : ids) {
                ps.setString(idx++, id);
            }
            ps.executeUpdate();
        }

        redirectBack(req, resp);
        return null; // view 없음 (redirect)
    }

    private void redirectBack(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String referer = req.getHeader("Referer");
        resp.sendRedirect(referer != null ? referer : (req.getContextPath() + "/"));
    }
}
