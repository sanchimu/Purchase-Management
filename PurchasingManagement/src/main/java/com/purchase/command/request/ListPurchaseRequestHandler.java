package com.purchase.command.request;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.request.PurchaseRequestService;
import com.purchase.vo.PurchaseRequest;

import mvc.command.CommandHandler;

public class ListPurchaseRequestHandler implements CommandHandler {

    private final PurchaseRequestService service = new PurchaseRequestService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

    	//// 요청 메서드가 GET이면 → 목록 조회 ((リクエストメソッドがGETの場合 → 購入リクエストリスト照会)
        if (req.getMethod().equalsIgnoreCase("GET")) {
            List<PurchaseRequest> list = service.getPurchaseRequestList();

          //목록이 비어있으면 메시지 설정 후 등록 화면으로 이동 (一覧が空ならメッセージを設定して登録画面へ移動)
            if (list.isEmpty()) {
                req.setAttribute("message", "등록된 구매 요청이 없습니다. 새 요청을 등록하세요.");
                return "/WEB-INF/view/purchaseRequestForm.jsp";
            }

            // 조회 결과를 JSP에서 사용하도록 저장 (取得結果をJSPで利用できるように保存)
            req.setAttribute("requestList", list);

            // 목록 JSP 반환 (一覧JSPを返す)
            return "/WEB-INF/view/purchaseRequestList.jsp";

        } else {
            // GET 외의 요청은 허용하지 않음 (GET以外のリクエストは許可しない)
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
    }
}