package com.purchase.command.ReceiveInfo;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import mvc.command.CommandHandler;

// 入庫情報のステータスを更新するためのハンドラー
// 입고 정보의 상태를 일괄 업데이트하는 핸들러
public class UpdateReceiveInfoHandler implements CommandHandler {

    // サービスクラスのインスタンス生成
    // 서비스 클래스 인스턴스 생성
    private final ReceiveInfoService service = new ReceiveInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        
        // === 1) POSTリクエスト以外は許可しない ===
        // POST 요청이 아닌 경우는 허용하지 않음 → 405 에러 반환
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }

        // === 2) 選択された入庫IDを取得 ===
        // 체크박스에서 선택된 입고 ID들을 가져옴
        String[] ids = req.getParameterValues("receiveIds");

        // ステータス値を格納するリスト
        // 상태 값들을 담을 리스트
        List<String> statuses = new ArrayList<>();

        if (ids != null) {
            // === 3) 各IDに対応するステータスを取得 ===
            // 각 ID에 해당하는 상태 값을 파라미터에서 가져옴
            for (String id : ids) {
                // JSP側では name="status_${r.receive_id}" という形式で送信される
                // JSP 쪽에서는 name="status_${r.receive_id}" 형식으로 전송됨
                statuses.add(req.getParameter("status_" + id));
            }

            // === 4) サービスを通じてDBのステータスを一括更新 ===
            // 서비스 호출 → DB의 상태값들을 일괄 업데이트
            service.updateStatuses(ids, statuses);
        }

        // === 5) 更新後、入庫一覧ページにリダイレクト ===
        // 업데이트 후 → 입고 목록 페이지로 리다이렉트
        res.sendRedirect(req.getContextPath() + "/listReceiveInfos.do");
        return null;
    }
}

