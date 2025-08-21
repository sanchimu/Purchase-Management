package com.purchase.command.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.request.PurchaseRequestService;
import com.purchase.vo.PurchaseRequest;
import mvc.command.CommandHandler;

public class PurchaseRequestQtyHandler implements CommandHandler {

    private final PurchaseRequestService service = new PurchaseRequestService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            String requestId = req.getParameter("request_id");
            PurchaseRequest pr = service.getById(requestId);
            req.setAttribute("req", pr);
            return "/WEB-INF/view/purchaseRequestQtyForm.jsp";
        }

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            String requestId = req.getParameter("request_id");
            int quantity = Integer.parseInt(req.getParameter("quantity"));

            service.updatePurchaseRequestQuantity(requestId, quantity);

            // 알림 후 이동 트리거를 JSP가 실행하도록 플래그/메시지만 심고 forward
            req.setAttribute("success", true);
            // 또는 커스텀 메시지
            // req.setAttribute("successMsg", "数量を修正しました。");

            return "/WEB-INF/view/purchaseRequestQtyForm.jsp";
        }
        res.setStatus(405);
        return null;
    }
}
