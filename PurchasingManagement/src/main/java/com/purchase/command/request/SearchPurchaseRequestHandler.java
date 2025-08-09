package com.purchase.command.request;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.request.PurchaseRequestService;
import com.purchase.vo.PurchaseRequest;

import mvc.command.CommandHandler;

public class SearchPurchaseRequestHandler implements CommandHandler {

    private final PurchaseRequestService service = new PurchaseRequestService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        if (req.getMethod().equalsIgnoreCase("GET")) {
        	// 요청 메서드가 GET이면 → 검색 폼 표시 ((リクエストメソッドがGETの場合 → 検索フォーム表示)
            return "/WEB-INF/view/purchaseRequestSearch.jsp";

        } else if (req.getMethod().equalsIgnoreCase("POST")) {
            // 요청 메서드가 POST면 → 검색 실행 (リクエストメソッドがPOSTの場合 → 検索実行)
            String productId = req.getParameter("product_id");

            if (productId == null || productId.trim().isEmpty()) {
                // 검색어가 없으면 → 검색 화면 유지 + 메시지 (検索語がない場合 → 検索画面に留まり、メッセージを表示)
                req.setAttribute("message", "검색어를 입력하세요.");
                return "/WEB-INF/view/purchaseRequestSearch.jsp";
            }

            List<PurchaseRequest> list = service.searchByProductId(productId.trim());

          //목록이 비어있으면 메시지 설정 후 등록 화면으로 이동 (一覧が空ならメッセージを設定して登録画面へ移動)
            if (list.isEmpty()) {
                req.setAttribute("message", "등록된 구매 요청이 없습니다. 새 요청을 등록하세요.");
                return "/WEB-INF/view/purchaseRequestForm.jsp";
            }

            // 결과 전달 (結果を渡す)
            req.setAttribute("product_id", productId);
            req.setAttribute("requestList", list);

            return "/WEB-INF/view/purchaseRequestSearch.jsp";

        } else {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
    }
}