package com.purchase.command.request;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.request.PurchaseRequestService;

import mvc.command.CommandHandler;

public class UpdatePurchaseRequestHandler implements CommandHandler {

    private final PurchaseRequestService service = new PurchaseRequestService();

    private static final List<String> ALLOWED =
            Arrays.asList("受付","検討中","承認","差し戻し","取り消し","完了");

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        req.setCharacterEncoding("UTF-8");

        // 폼에서 선택된 요청ID 배열을 생성
        // フォームで選択されたリクエストID配列を生成
        String[] ids = req.getParameterValues("ids"); 
        if (ids == null || ids.length == 0) {
            return redirectBack(req, res);
        }

        // 요청ID→새 상태 매핑 (입력 순서 보존) 
        // 依頼ID→新状態のマッピング（入力順を維持）
        Map<String,String> idToStatus = new LinkedHashMap<>();
        
        // 중복 ID 제거 후 순회 
        // 重複IDを除去して走査
        Arrays.stream(ids).distinct().forEach(id -> {
            String newStatus = req.getParameter(id);
            
            // null이 아니고 허용 리스트에 있으면 일괄 변경 대상으로 등록 
            // nullでなく許可リストにあれば一括変更の対象として登録
            if (newStatus != null && ALLOWED.contains(newStatus)) {
                idToStatus.put(id, newStatus);
            }
        });

        // 변경할 항목이 있을 때만 업데이트 실행 
        // 変更対象がある場合のみ更新を実行
        if (!idToStatus.isEmpty()) {
            service.updateRequestStatusBulk(idToStatus);
        }

        return redirectBack(req, res);
    }

    private String redirectBack(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String referer = req.getHeader("Referer");
        res.sendRedirect(referer != null ? referer : (req.getContextPath() + "/requestList.do"));
        return null;
    }
}
