package com.purchase.command.order;

import com.purchase.service.order.OrderSheetService;
import mvc.command.CommandHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 발주서 표시상태 재개(A) 일괄 처리 핸들러
 * 発注書の表示状態 再開（A）一括処理ハンドラ
 *
 *  POST만 허용 / POSTのみ許可
 *  체크된 행의 row_status 를 'A'로 변경 / チェック行の row_status を 'A' に更新
 *  목록의 "중단 포함" 필터 유지 / 一覧の「停止含む」フィルタを維持
 */
public class OrderSheetRowStatusResumeHandler implements CommandHandler {
    private final OrderSheetService service = new OrderSheetService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // 메서드制限 / メソッド制限
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
        req.setCharacterEncoding("UTF-8");

        // 목록 화면 필터 상태 유지용 / 一覧画面のフィルタ保持用
        String includeHidden = req.getParameter("includeHidden");

        // 선택된 발주ID / 選択された発注ID
        String[] idsParam = req.getParameterValues("orderIds");

        // 안전 정제(공백제거·중복제거·빈값제거) / 安全整形（空白・重複・空値の除去）
        Set<String> ids = new LinkedHashSet<>();
        if (idsParam != null) {
            for (String s : idsParam) {
                if (s == null) continue;
                String t = s.trim();
                if (!t.isEmpty()) ids.add(t);
            }
        }

        // 선택이 없으면 조용히 목록으로 / 未選択なら静かに一覧へ
        if (!ids.isEmpty()) {
            try {
                // 일괄 재개(A) / 一括で再開（A）
                service.setRowStatusBulk(ids.toArray(new String[0]), "A");
            } catch (RuntimeException ex) {
                // 일부 실패는 서비스에서 처리/로그 / 一部失敗はサービス側で処理/ログ
                // 필요 시: req.setAttribute("error", "再開中に一部失敗: " + ex.getMessage());
            }
        }

        // PRG: 목록으로 리다이렉트 (+필터 유지)
        // PRG: 一覧へリダイレクト（＋フィルタ保持）
        String to = req.getContextPath() + "/orderSheetList.do";
        if ("1".equals(includeHidden)) {
            to += "?includeHidden=1";
        }
        res.sendRedirect(to);
        return null;
    }
}
