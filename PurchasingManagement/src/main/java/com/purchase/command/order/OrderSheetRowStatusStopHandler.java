package com.purchase.command.order;

import com.purchase.service.order.OrderSheetService;
import mvc.command.CommandHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 발주서 표시상태 중단(X) 일괄 처리 핸들러
 * 発注書の表示状態 停止（X）一括処理ハンドラ
 *
 *  POST만 허용 / POSTのみ許可
 *  체크된 행의 row_status 를 'X'로 변경 / チェック行の row_status を 'X' に更新
 *  목록의 "중단 포함" 필터 유지 / 一覧の「停止含む」フィルタを維持
 */
public class OrderSheetRowStatusStopHandler implements CommandHandler {
    private final OrderSheetService service = new OrderSheetService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // 메서드 제한 / メソッド制限
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
        req.setCharacterEncoding("UTF-8");

        // 목록 화면 필터 상태 유지용 / 一覧画面のフィルタ保持用
        String includeHidden = req.getParameter("includeHidden");

        // 선택된 발주ID / 選択された発注ID
        String[] idsParam = req.getParameterValues("orderIds");

        // 안전 정제(공백·중복·빈값 제거) / 安全整形（空白・重複・空値の除去）
        Set<String> ids = new LinkedHashSet<>();
        if (idsParam != null) {
            for (String s : idsParam) {
                if (s == null) continue;
                String t = s.trim();
                if (!t.isEmpty()) ids.add(t);
            }
        }

        // 선택 없으면 조용히 목록으로 / 未選択なら静かに一覧へ
        if (!ids.isEmpty()) {
            try {
                // 일괄 중단(X) / 一括で停止（X）
                service.setRowStatusBulk(ids.toArray(new String[0]), "X");
            } catch (RuntimeException ex) {
                // 부분 실패는 서비스에서 처리/로그 / 一部失敗はサービス側で処理・ログ
                // 必要なら: req.setAttribute("error", "停止中に一部失敗: " + ex.getMessage());
            }
        }

        // PRG: 목록으로 리다이렉트 (+필터 유지) / PRG: 一覧へリダイレクト（＋フィルタ保持）
        String to = req.getContextPath() + "/orderSheetList.do";
        if ("1".equals(includeHidden)) {
            to += "?includeHidden=1";
        }
        res.sendRedirect(to);
        return null;
    }
}
