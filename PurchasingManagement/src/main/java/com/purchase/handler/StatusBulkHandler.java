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

	// 허용 테이블과 PK 화이트리스트 (SQL 인젝션 방지) / 許可テーブルとPKホワイトリスト（SQLインジェクション防止）
    private static final Map<String, String> ALLOWED = new HashMap<>();
    static {
        ALLOWED.put("supplier_info",    "supplier_id");
        ALLOWED.put("product",          "product_id");
        ALLOWED.put("purchase_request", "request_id");
        ALLOWED.put("order_sheet",      "order_id");
    }

    @Override
    public String process(HttpServletRequest req, HttpServletResponse resp) throws Exception {
    	String to = req.getParameter("to");                 // "A" 또는 "X" / 「A」または「X」
        String table = req.getParameter("table");           // 대상 테이블명 / 対象テーブル名
        String idCol = req.getParameter("idColumn");        // PK 컬럼명 / PKカラム名
        String[] ids = req.getParameterValues("ids");       // 선택된 ID 목록 / 選択されたIDリスト


        // 소문자로 변환 (폼에서 대문자로 와도 안전하게 처리) / 小文字に変換（フォームから大文字で来ても安全に処理）
        if (table != null) table = table.toLowerCase(Locale.ROOT).trim();
        if (idCol != null) idCol = idCol.toLowerCase(Locale.ROOT).trim();

        // 기본 검증 / 基本検証
        if (!"A".equals(to) && !"X".equals(to)) {
            redirectBack(req, resp);
            return null;
        }
        if (table == null || idCol == null || ids == null || ids.length == 0) {
            redirectBack(req, resp);
            return null;
        }

        // 화이트리스트 체크 / ホワイトリスト確認
        String allowedPk = ALLOWED.get(table);
        if (allowedPk == null || !allowedPk.equals(idCol)) {
            redirectBack(req, resp);
            return null;
        }

        // ? 자리수 생성 / プレースホルダ生成
        String placeholders = String.join(",", Collections.nCopies(ids.length, "?"));
        String sql = "UPDATE " + table + " SET row_status=? WHERE " + idCol + " IN (" + placeholders + ")";
        // DB 연결 및 업데이트 실행 / DB接続と更新実行
        try (Connection con = ConnectionProvider.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, to);  // row_status 값 지정 / row_status 値を指定
            int idx = 2;
            for (String id : ids) {
                ps.setString(idx++, id);  // ID 바인딩 / IDバインド
            }
            ps.executeUpdate();  // SQL 실행 / SQL実行
        }

        redirectBack(req, resp);
        return null; // view 없음 (리다이렉트 처리) / ビューなし（リダイレクト処理）
    }
    // 이전 페이지로 리다이렉트 / 前のページへリダイレクト
    private void redirectBack(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String referer = req.getHeader("Referer");  // 요청 헤더의 Referer / リクエストヘッダのReferer
        resp.sendRedirect(referer != null ? referer : (req.getContextPath() + "/"));
    }
}
