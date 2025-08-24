package com.purchase.command.ReceiveInfo;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import mvc.command.CommandHandler;

// 入庫ステータスをまとめて更新するハンドラー
// 입고 상태를 한 번에 바꾸는 핸들러
public class UpdateReceiveInfoHandler implements CommandHandler {

    // 実処理はサービスへ任せる。ここは受け渡し役
    // 실제 처리는 서비스에 맡기고, 여기는 값 받아서 넘기는 역할
    private final ReceiveInfoService service = new ReceiveInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        
        // 1) POSTだけ許可。GETで来たら 405 を返す
        // 1) POST 요청만 허용. GET 등으로 오면 405 응답
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }

        // 2) チェックされた入庫IDを取得（name="receiveIds" のチェックボックス）
        // 2) 체크된 입고 ID 모으기(name="receiveIds" 체크박스)
        String[] ids = req.getParameterValues("receiveIds");

        // ステータス値を詰める箱
        // 상태 값 담아둘 리스트
        List<String> statuses = new ArrayList<>();

        if (ids != null) {
            // 3) 各IDに対するステータスをフォームから取り出す
            // 3) 각 ID에 대응하는 상태를 폼에서 꺼내기
            for (String id : ids) {
                // フロントは name="status_${r.receive_id}" の形で送ってくる
                // 프론트에서 name="status_${r.receive_id}" 형태로 보냄
                statuses.add(req.getParameter("status_" + id));
            }

            // 4) サービス経由でDBを一括更新
            // 4) 서비스 호출로 DB 상태값 일괄 업데이트
            service.updateStatuses(ids, statuses);
        }

        // 5) 終わったら一覧に戻す（リダイレクト）
        // 5) 끝나면 목록으로 리다이렉트
        res.sendRedirect(req.getContextPath() + "/listReceiveInfos.do");
        return null;
    }
}

