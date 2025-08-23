package com.purchase.command.order;

import com.purchase.service.order.OrderSheetService;
import com.purchase.vo.OrderSheet;
import mvc.command.CommandHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 발주서 상태 일괄변경 / 発注書ステータス一括更新
 *  화면에서 체크된 행만 업데이트 / 画面でチェックされた行のみ更新
 *  상태값은 서비스가 제공하는 허용 목록(=DB CHECK)만 반영 / サービスの許容リスト（=DB CHECK）のみ反映
 */
public class UpdateOrderSheetHandler implements CommandHandler {

    private final OrderSheetService service = new OrderSheetService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // POST만 허용 / POSTのみ許可
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
        req.setCharacterEncoding("UTF-8");

        // 목록 화면의 필터상태(중단 포함) 유지 / 一覧画面のフィルタ状態（停止含む）維持
        String includeHidden = req.getParameter("includeHidden");

        // 체크된 발주ID들 / チェックされた発注ID
        String[] orderIds = req.getParameterValues("orderIds");
        if (orderIds != null && orderIds.length > 0) {
            // 허용 상태값(일본어) / 許容ステータス（日本語）
            List<String> allowed = service.getOrderStatusList();
            Set<String> allowSet = new HashSet<>(allowed);

            for (String id : orderIds) {
                if (id == null || id.isBlank()) continue;

                // name="status_{order_id}" 로 넘어온 값 / name="status_{order_id}" の値
                String st = req.getParameter("status_" + id);

                // 빈 값/미허용 값은 스킵(제약 위반 예방) / 空・非許容はスキップ（制約違反防止）
                if (st == null || st.isBlank() || !allowSet.contains(st)) continue;

                // 최소필드로 업데이트 / 最小項目で更新
                OrderSheet os = new OrderSheet();
                os.setOrder_id(id);
                os.setOrder_status(st);

                // 개별 업데이트(부분 실패 허용) / 個別更新（部分失敗を許容）
                try {
                    service.updateOrderStatus(os);
                } catch (RuntimeException ex) {
                    // 로깅 등으로 흘려보내고 계속 / ログ等に残して続行
                    // Logger.warn("Order status update skipped: id=" + id + ", msg=" + ex.getMessage());
                }
            }
        }

        // 목록으로 PRG 리다이렉트(+필터 유지) / 一覧へPRGリダイレクト（＋フィルタ保持）
        String to = req.getContextPath() + "/orderSheetList.do";
        if ("1".equals(includeHidden)) {
            to += "?includeHidden=1";
        }
        res.sendRedirect(to);
        return null;
    }
}
