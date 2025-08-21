package com.purchase.command.ReceiveInfo;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import mvc.command.CommandHandler;

// 入庫情報のステータスを更新するためのハンドラー
public class UpdateReceiveInfoHandler implements CommandHandler {
    private final ReceiveInfoService service = new ReceiveInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // POSTリクエスト以外は許可しない (405エラーを返す)
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }

        // 複数の入庫IDを取得 (チェックボックスなどから)
        String[] ids = req.getParameterValues("receiveIds");

        // ステータス値を格納するリスト
        List<String> statuses = new ArrayList<>();

        if (ids != null) {
            // 各IDごとに対応するステータスを取得
            for (String id : ids) {
                // JSP側では name="status_${r.receive_id}" という形式で送信される
                statuses.add(req.getParameter("status_" + id));
            }
            // サービスを通じてDBのステータスを一括更新
            service.updateStatuses(ids, statuses);
        }

        // 更新処理が終わったら入庫一覧ページにリダイレクト
        res.sendRedirect(req.getContextPath() + "/listReceiveInfos.do");
        return null;
    }
}

