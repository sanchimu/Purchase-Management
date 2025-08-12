package com.purchase.command.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.request.PurchaseRequestService;

import mvc.command.CommandHandler;

public class DeletePurchaseRequestHandler implements CommandHandler {

    private PurchaseRequestService requestService = new PurchaseRequestService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        if (req.getMethod().equalsIgnoreCase("POST")) { //요청 방식이 POST인지 확인 (リクエストメソッドがPOSTか確認)
            String requestId = req.getParameter("request_id"); //삭제할 구매 요청 ID를 파라미터에서 꺼냄 (削除対象のリクエストIDをパラメータから取得)

            //requset_id가 null이 아니고 공백도 아닐 때만 삭제 실행(request_id が null でも空白でもない場合のみ削除を実行)
            if (requestId != null && !requestId.trim().isEmpty()) {
                requestService.removePurchaseRequest(requestId);
            }

            //삭제 후 목록 페이지로 리다이렉트 (削除後、リストページにリダイレクト)
            res.sendRedirect(req.getContextPath() + "/requestList.do");
            return null;
        } else { //POST 요청이 아니라면 에러 반환 ( POSTリクエスト以外はエラーを返す)
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
    }
}